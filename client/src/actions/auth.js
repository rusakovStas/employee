import jwtDecode from "jwt-decode";
import { USER_LOGGED_IN, USER_LOGGED_OUT, USER_ROLES } from "./types";
import api from "../api/api";
import setAuthToken from "../utils/setAuthToken";

export const userLoggedIn = user => ({
	type: USER_LOGGED_IN,
	user
});

export const userLoggedOut = () => ({
	type: USER_LOGGED_OUT
});

export const userRoles = roles => ({
	type: USER_ROLES,
	roles
});

export const login = cred => dispatch =>
	api.user.login(cred).then(token => {
		const user = {
			access_token: token,
			roles: jwtDecode(token).rol
		};
		localStorage.tokenJWT = token;
		setAuthToken(token);
		dispatch(userLoggedIn(user));
		dispatch(userRoles(jwtDecode(token).rol));
	});

export const logout = () => dispatch => {
	localStorage.removeItem("tokenJWT");
	localStorage.removeItem("activeTabName");
	localStorage.removeItem("roles");
	setAuthToken();
	dispatch(userLoggedOut());
};
