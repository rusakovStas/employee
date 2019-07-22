import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import {
	getEmployees,
	createEmployee,
	editEmployee,
	deleteEmployees
} from "../../actions/employees";
import EmployeeForm from "./Form";

class EmployeePage extends React.Component {
	state = {};

	componentDidMount() {
		this.props.getEmployees();
	}

	render() {
		return (
			<div>
				<EmployeeForm
					delete={this.props.deleteEmployees}
					create={this.props.createEmployee}
					edit={this.props.editEmployee}
					employees={this.props.employees}
					hasRoleAdmin={this.props.hasRoleAdmin}
				/>
			</div>
		);
	}
}

EmployeePage.propTypes = {
	employees: PropTypes.arrayOf(PropTypes.object).isRequired,
	hasRoleAdmin: PropTypes.bool.isRequired,
	getEmployees: PropTypes.func.isRequired,
	createEmployee: PropTypes.func.isRequired,
	editEmployee: PropTypes.func.isRequired,
	deleteEmployees: PropTypes.func.isRequired
};

function mapStateToProps(state) {
	return {
		employees: state.employees,
		hasRoleAdmin:
			!!state.user.roles &&
			!!state.user.roles.find(element => element === "ROLE_ADMIN")
	};
}

export default connect(
	mapStateToProps,
	{
		getEmployees,
		createEmployee,
		editEmployee,
		deleteEmployees
	}
)(EmployeePage);
