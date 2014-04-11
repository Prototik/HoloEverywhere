package org.holoeverywhere.resbuilder.api

import org.holoeverywhere.resbuilder.tasks.ResbuilderDefaultTask

abstract class PathType<T extends IPathState> implements Type {
    public static interface IPathState extends Flushable {
        def void prepare()
    }

    class PathState<T> {
        private final Map<String, T> map = new HashMap<>()

        def obtain(String path, Closure<?> action, Closure<T> create) {
            T t = map.get(path)
            if (t == null) {
                map.put(path, t = create.call(path))
            }
            action = action.clone() as Closure<?>
            action.delegate = t
            action.call(t)
            return t
        }

        def obtain(File file, Closure<?> action, Closure<T> create) {
            return obtain(file.absolutePath, action, create)
        }
    }

    PathState<T> state
    File output

    @Override
    def Object process(Map<String, ?> data, File output, Object stateRaw, File input) {
        PathState<T> state = stateRaw as PathState<T>
        if (state == null) {
            state = new PathState<>()
        }
        final File path = obtainPath(output, data, input)
        this.state = state
        this.output = output
        obtainState(path, { T t -> process(data, t, input) })
        return state
    }

    File obtainPath(File output, Map<String, ?> data, File input) {
        return new File(output, input.getName())
    }

    def T obtainState(File file, Closure<?> action) {
        return obtainState(file, action, { return createState(file) })
    }

    def T obtainState(File file, Closure<?> action, Closure<T> create) {
        return state?.obtain(file, action, create)
    }

    @Override
    void flush(Object stateRaw) {
        PathState<T> state = stateRaw as PathState<T>
        if (state == null) return
        state.map.values().each { T t -> t.prepare() }
        state.map.values().each { T t -> t.flush() }
    }

    def abstract process(Map<String, ?> data, T state, File input)

    def abstract T createState(File path)

    @Override
    void bind(ResbuilderDefaultTask task) {
    }
}
