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
				item => item.employeeid === action.employee.employeeid
			) === -1
				? state.concat(action.employee)
				: state;
		case UPDATE_EMPLOYEE:
			return state.map(item =>
				item.employeeid === action.employee.employeeid
					? {
							...item,
							salary: action.employee.salary,
							name: action.employee.name
					  }
					: item
			);
		case DELETE_EMPLOYEES:
			return [];
		default:
			return state;
	}
}
