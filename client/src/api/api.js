import axios from "axios";
import BASE_URL from "./constants";

export default {
	user: {
		login: cred => {
			const config = {
				headers: { Authorization: "" }
			};
			return axios
				.post(
					`http://${BASE_URL}/authenticate?username=${
						cred.email
					}&password=${cred.password}`,
					"",
					config
				)
				.then(res => res.headers.authorization);
		}
	},
	admin: {
		deleteAllEmployees: () =>
				axios.delete(`http://${BASE_URL}/employees`).then(res => res.data),
		getAllUsers: () =>
			axios.get(`http://${BASE_URL}/users/all`).then(res => res.data),
		addUser: user =>
			axios
				.post(`http://${BASE_URL}/users`, user)
				.then(response => response.data),
		deleteUser: user =>
			axios
				.delete(`http://${BASE_URL}/users?username=${user.username}`)
				.then(response => response.data)
	},
	employees: {
		getAllEmployees: () =>
			axios.get(`http://${BASE_URL}/employees`).then(res => res.data),
		createEmployee: employee =>
			axios
				.post(`http://${BASE_URL}/employees`, employee)
				.then(res => res.data),
		editEmployee: employee =>
			axios
				.put(`http://${BASE_URL}/employees`, employee)
				.then(res => res.data)
	}
};
