package mock;

import org.activiti.engine.EngineServices;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.VariableInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DeMock implements DelegateExecution {

    public Map<String, Object> variables = new HashMap<>();

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getProcessInstanceId() {
        return null;
    }

    @Override
    public String getEventName() {
        return null;
    }

    @Override
    public String getBusinessKey() {
        return null;
    }

    @Override
    public String getProcessBusinessKey() {
        return null;
    }

    @Override
    public String getProcessDefinitionId() {
        return null;
    }

    @Override
    public String getParentId() {
        return null;
    }

    @Override
    public String getSuperExecutionId() {
        return null;
    }

    @Override
    public String getCurrentActivityId() {
        return null;
    }

    @Override
    public String getCurrentActivityName() {
        return null;
    }

    @Override
    public String getTenantId() {
        return null;
    }

    @Override
    public EngineServices getEngineServices() {
        return null;
    }

    @Override
    public Map<String, Object> getVariables() {
        return null;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstances() {
        return null;
    }

    @Override
    public Map<String, Object> getVariables(Collection<String> collection) {
        return null;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstances(Collection<String> collection) {
        return null;
    }

    @Override
    public Map<String, Object> getVariables(Collection<String> collection, boolean b) {
        return null;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstances(Collection<String> collection, boolean b) {
        return null;
    }

    @Override
    public Map<String, Object> getVariablesLocal() {
        return null;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstancesLocal() {
        return null;
    }

    @Override
    public Map<String, Object> getVariablesLocal(Collection<String> collection) {
        return null;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstancesLocal(Collection<String> collection) {
        return null;
    }

    @Override
    public Map<String, Object> getVariablesLocal(Collection<String> collection, boolean b) {
        return null;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstancesLocal(Collection<String> collection, boolean b) {
        return null;
    }

    @Override
    public Object getVariable(String s) {
        return variables.get(s);
    }

    @Override
    public VariableInstance getVariableInstance(String s) {
        return null;
    }

    @Override
    public Object getVariable(String s, boolean b) {
        return null;
    }

    @Override
    public VariableInstance getVariableInstance(String s, boolean b) {
        return null;
    }

    @Override
    public Object getVariableLocal(String s) {
        return null;
    }

    @Override
    public VariableInstance getVariableInstanceLocal(String s) {
        return null;
    }

    @Override
    public Object getVariableLocal(String s, boolean b) {
        return null;
    }

    @Override
    public VariableInstance getVariableInstanceLocal(String s, boolean b) {
        return null;
    }

    @Override
    public <T> T getVariable(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T getVariableLocal(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public Set<String> getVariableNames() {
        return null;
    }

    @Override
    public Set<String> getVariableNamesLocal() {
        return null;
    }

    @Override
    public void setVariable(String s, Object o) {
        variables.put(s, o);
    }

    @Override
    public void setVariable(String s, Object o, boolean b) {

    }

    @Override
    public Object setVariableLocal(String s, Object o) {
        return null;
    }

    @Override
    public Object setVariableLocal(String s, Object o, boolean b) {
        return null;
    }

    @Override
    public void setVariables(Map<String, ? extends Object> map) {

    }

    @Override
    public void setVariablesLocal(Map<String, ? extends Object> map) {

    }

    @Override
    public boolean hasVariables() {
        return false;
    }

    @Override
    public boolean hasVariablesLocal() {
        return false;
    }

    @Override
    public boolean hasVariable(String s) {
        return false;
    }

    @Override
    public boolean hasVariableLocal(String s) {
        return false;
    }

    @Override
    public void createVariableLocal(String s, Object o) {

    }

    @Override
    public void removeVariable(String s) {

    }

    @Override
    public void removeVariableLocal(String s) {

    }

    @Override
    public void removeVariables(Collection<String> collection) {

    }

    @Override
    public void removeVariablesLocal(Collection<String> collection) {

    }

    @Override
    public void removeVariables() {

    }

    @Override
    public void removeVariablesLocal() {

    }
}
