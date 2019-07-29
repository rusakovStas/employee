import {
	GET_EMPLOYEES,
	ADD_EMPLOYEE,
	UPDATE_EMPLOYEE,
	DELETE_EMPLOYEES
} from "./types";
import api from "../api/api";
import webSocket from "../api/web-socket";

export const getEmployeesFromRs = employees => ({
	type: GET_EMPLOYEES,
	employees
});

export const addEmployee = employee => ({
	type: ADD_EMPLOYEE,
	employee
});

export const updateEmployee = employee => ({
	type: UPDATE_EMPLOYEE,
	employee
});

export const del = () => ({
	type: DELETE_EMPLOYEES
});

export const getEmployees = (onConnect, onError) => dispatch => {
	api.employees.getAllEmployees().then(employees => {
		dispatch(getEmployeesFromRs(employees));
	});
	webSocket.stompClient.onEvent(
		onConnect,
		`/topic/push`,
		() => {
			api.employees.getAllEmployees().then(employees => {
				dispatch(getEmployeesFromRs(employees));
			});
		},
		onError
	);
};

export const deleteEmployees = () => dispatch =>
	api.admin.deleteAllEmployees().then(() => {
		dispatch(del());
	});

export const createEmployee = employee => dispatch =>
	api.employees.createEmployee(employee).then(response => {
		dispatch(addEmployee(response));
	});

export const editEmployee = employee => dispatch =>
	api.employees.editEmployee(employee).then(response => {
		dispatch(updateEmployee(response));
	});
