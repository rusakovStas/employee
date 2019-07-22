import axios from "axios";
import jwtDecode from "jwt-decode";

export default (token = null) => {
	if (token) {
		localStorage.roles = JSON.stringify(jwtDecode(token).rol);
		axios.defaults.headers.common.Authorization = `Bearer ${token}`;
	} else {
		delete axios.defaults.headers.common.Authorization;
	}
};
