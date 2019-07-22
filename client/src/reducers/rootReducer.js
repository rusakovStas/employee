import { combineReducers } from "redux";
import user from "./user";
import users from "./users";
import employees from "./employees";

export default combineReducers({
	user,
	users,
	employees
});
