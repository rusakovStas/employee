package common;

public interface PreConditionExecutor {
    void executeAndAddToQueueToUndo(PreCondition preCondition);

    void undoAll();
}
