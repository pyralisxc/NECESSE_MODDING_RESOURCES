/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.agent.builder.AgentBuilder$Default
 *  net.bytebuddy.agent.builder.AgentBuilder$RedefinitionStrategy
 *  net.bytebuddy.agent.builder.AgentBuilder$RedefinitionStrategy$Listener
 *  net.bytebuddy.asm.Advice
 *  net.bytebuddy.asm.AsmVisitorWrapper
 *  net.bytebuddy.dynamic.DynamicType$Builder
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatchers
 */
package necesse.engine.modLoader.classes;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModClassSignature;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModCustomPatch;
import necesse.engine.modLoader.annotations.ModCustomPatchMethod;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.modLoader.classes.ModClass;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class ModPatchClasses
extends ModClass {
    private final List<Class<?>> modClasses = new LinkedList();
    private LoadedMod mod;

    @Override
    public boolean shouldRegisterModClass(Class<?> modClass) {
        return modClass.isAnnotationPresent(ModMethodPatch.class) || modClass.isAnnotationPresent(ModConstructorPatch.class) || modClass.isAnnotationPresent(ModCustomPatch.class);
    }

    @Override
    public void registerModClass(LoadedMod mod, Class<?> modClass) throws ModLoadException {
        this.modClasses.add(modClass);
    }

    @Override
    public void finalizeLoading(LoadedMod mod) throws ModLoadException {
        this.mod = mod;
    }

    public void applyPatches(ComputedValue<Instrumentation> instrumentation, HashSet<ModClassSignature> editedSignatures) throws ModLoadException {
        LinkedList prioritizedPatches = new LinkedList();
        for (Class<?> clazz : this.modClasses) {
            try {
                Annotation annotation;
                if (clazz.isAnnotationPresent(ModMethodPatch.class)) {
                    annotation = clazz.getAnnotation(ModMethodPatch.class);
                    String argumentsString = Arrays.stream(annotation.arguments()).map(Class::getSimpleName).collect(Collectors.joining(", "));
                    String string = "." + annotation.name() + "(" + argumentsString + ")";
                    GameUtils.insertSortedList(prioritizedPatches, new PrioritizedPatchClass(clazz, annotation.target(), annotation.name(), annotation.arguments(), annotation.priority(), string, arg_0 -> ModPatchClasses.lambda$applyPatches$0(clazz, (ModMethodPatch)annotation, arg_0)), Comparator.comparingInt(p -> -p.priority));
                    continue;
                }
                if (clazz.isAnnotationPresent(ModConstructorPatch.class)) {
                    annotation = clazz.getAnnotation(ModConstructorPatch.class);
                    String argumentsString = Arrays.stream(annotation.arguments()).map(Class::getSimpleName).collect(Collectors.joining(", "));
                    String string = ".<constructor>(" + argumentsString + ")";
                    GameUtils.insertSortedList(prioritizedPatches, new PrioritizedPatchClass(clazz, annotation.target(), "<init>", annotation.arguments(), annotation.priority(), string, arg_0 -> ModPatchClasses.lambda$applyPatches$2(clazz, (ModConstructorPatch)annotation, arg_0)), Comparator.comparingInt(p -> -p.priority));
                    continue;
                }
                if (!clazz.isAnnotationPresent(ModCustomPatch.class)) continue;
                annotation = clazz.getAnnotation(ModCustomPatch.class);
                boolean found = false;
                for (Method method : clazz.getDeclaredMethods()) {
                    if (!method.isAnnotationPresent(ModCustomPatchMethod.class) || method.getParameterCount() != 1 || method.getParameterTypes()[0] != Advice.class || method.getReturnType() != AsmVisitorWrapper.class) continue;
                    GameUtils.insertSortedList(prioritizedPatches, new PrioritizedPatchClass(clazz, annotation.target(), null, null, annotation.priority(), " with custom patch", builder -> {
                        try {
                            return builder.visit((AsmVisitorWrapper)method.invoke(null, Advice.to((Class)clazz)));
                        }
                        catch (IllegalAccessException | InvocationTargetException e) {
                            throw new PatchBuilderException(e);
                        }
                    }), Comparator.comparingInt(p -> -p.priority));
                    found = true;
                    break;
                }
                if (found) continue;
                throw new ModLoadException(this.mod, "@ModCustomPatch must have a static method annotated with @ModCustomPatchMethod taking an Advice.class parameter and returning an AsmVisitorWrapper object");
            }
            catch (ModLoadException e) {
                throw e;
            }
            catch (TypeNotPresentException e) {
                System.err.println("Error loading mod patch class \"" + clazz.getName() + "\" from likely outdated mod: " + this.mod.getModNameString());
                String message = e.getMessage();
                if (message != null && !message.isEmpty()) {
                    System.err.println("\t" + message);
                    continue;
                }
                e.printStackTrace();
            }
            catch (Exception e) {
                throw new ModLoadException(this.mod, "Error loading mod patch class: " + clazz.getName(), e);
            }
        }
        for (PrioritizedPatchClass patch : prioritizedPatches) {
            ModClassSignature signature = new ModClassSignature(patch.targetClass, patch.targetMethod, patch.methodArguments);
            if (ModLoader.isIllegalModPatch(signature)) {
                System.err.println(this.mod.id + " tried to perform patch on illegal signature: " + patch.targetClass.getName() + patch.callName);
                continue;
            }
            try {
                AtomicReference builderException = new AtomicReference();
                final AtomicInteger atomicInteger = new AtomicInteger();
                new AgentBuilder.Default().disableClassFormatChanges().with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION).with(new AgentBuilder.RedefinitionStrategy.Listener(){

                    public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
                    }

                    public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
                        return null;
                    }

                    public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
                        atomicInteger.addAndGet(amount);
                    }
                }).type((ElementMatcher)ElementMatchers.is(patch.targetClass)).transform((builder, typeDescription, classLoader, module) -> {
                    try {
                        return patch.builderFunc.apply(builder);
                    }
                    catch (Exception e) {
                        builderException.set(e);
                        return builder;
                    }
                }).installOn(instrumentation.get());
                instrumentation.get().retransformClasses(this.modClasses.toArray(new Class[1]));
                if (builderException.get() != null) {
                    Throwable exception = (Throwable)builderException.get();
                    if (exception instanceof PatchBuilderException && (exception = exception.getCause()) instanceof InvocationTargetException) {
                        exception = exception.getCause();
                    }
                    throw new ModLoadException(this.mod, "Error performing mod patch transformation", exception);
                }
                if (atomicInteger.get() > 0) {
                    System.out.println(this.mod.id + " transformed " + patch.targetClass.getName() + patch.callName);
                    editedSignatures.add(signature);
                    continue;
                }
                GameLog.warn.println(this.mod.id + " advice class \"" + patch.adviceClass.getName() + "\" did not complete any patches. Are you sure you're targeting the right class and method?");
            }
            catch (ModLoadException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ModLoadException(this.mod, "Error loading mod patch builder: " + patch.adviceClass.getName(), e);
            }
        }
    }

    private static /* synthetic */ DynamicType.Builder lambda$applyPatches$2(Class clazz, ModConstructorPatch annotation, DynamicType.Builder builder) {
        return builder.visit((AsmVisitorWrapper)Advice.to((Class)clazz).on((ElementMatcher)ElementMatchers.isConstructor().and((ElementMatcher)ElementMatchers.takesArguments((Class[])annotation.arguments()))));
    }

    private static /* synthetic */ DynamicType.Builder lambda$applyPatches$0(Class clazz, ModMethodPatch annotation, DynamicType.Builder builder) {
        return builder.visit((AsmVisitorWrapper)Advice.to((Class)clazz).on((ElementMatcher)ElementMatchers.isMethod().and((ElementMatcher)ElementMatchers.named((String)annotation.name())).and((ElementMatcher)ElementMatchers.takesArguments((Class[])annotation.arguments()))));
    }

    private static class PrioritizedPatchClass {
        public final Class<?> adviceClass;
        public final Class<?> targetClass;
        public final String targetMethod;
        public final Class<?>[] methodArguments;
        public final int priority;
        public final String callName;
        public final Function<DynamicType.Builder<?>, DynamicType.Builder<?>> builderFunc;

        public PrioritizedPatchClass(Class<?> adviceClass, Class<?> targetClass, String targetMethod, Class<?>[] methodArguments, int priority, String callName, Function<DynamicType.Builder<?>, DynamicType.Builder<?>> builderFunc) {
            this.adviceClass = adviceClass;
            this.targetClass = targetClass;
            this.targetMethod = targetMethod;
            this.methodArguments = methodArguments;
            this.priority = priority;
            this.callName = callName;
            this.builderFunc = builderFunc;
        }
    }

    private static class PatchBuilderException
    extends RuntimeException {
        public PatchBuilderException(Throwable cause) {
            super(cause);
        }
    }
}

