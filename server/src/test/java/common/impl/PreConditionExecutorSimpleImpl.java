package common.impl;

import common.PreCondition;
import common.PreConditionExecutor;

import java.util.ArrayList;
import java.util.List;

/*
* Класс который будет выполнять прекондишины с последующим их откатом
* Так как Junit изолирует тесты (для каждого потока as well) то можно не заморачиваться с Concurrency
* */
public class PreConditionExecutorSimpleImpl implements PreConditionExecutor {

    private List<PreCondition> preConditions = new ArrayList<>();

    @Override
    public void executeAndAddToQueueToUndo(PreCondition preCondition){
        preCondition.execute();
        preConditions.add(preCondition);
    }


    @Override
    public void undoAll(){
        preConditions.forEach(PreCondition::undo);
        preConditions.clear();
    }
}
