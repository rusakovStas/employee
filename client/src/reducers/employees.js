import {
	GET_EMPLOYEES,
	ADD_EMPLOYEE,
	UPDATE_EMPLOYEE,
	DELETE_EMPLOYEES
} from "../actions/types";

export default function employees(state = [], action) {
	switch (action.type) {
		case GET_EMPLOYEES:
			return action.employees;
		case ADD_EMPLOYEE:
			return state.findIndex(
				item => item.employeeId === action.employee.employeeId
			) === -1
				? state.concat(action.employee)
				: state;
		case UPDATE_EMPLOYEE:
			return state.map(item =>
				item.employeeId === action.employee.employeeId
					? {
							...item,
							salary: action.employee.salary
					  }
					: item
			);
		case DELETE_EMPLOYEES:
			return [];
		default:
			return state;
	}
}
