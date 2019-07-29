import React from "react";
import PropTypes from "prop-types";
import { Container, Card, Input, Row, Col, Alert, Spinner } from "reactstrap";
import NumberFormat from "react-number-format";
import FormButton from "../commons/FormButton";
import InlineError from "../commons/InlineError";
import Employee from "./Employee";

class EmployeeForm extends React.Component {
	state = {
		data: {
			name: "",
			salary: {
				amount: ""
			}
		},
		loading: false,
		errors: {}
	};

	onChange = e =>
		this.setState({
			data: { ...this.state.data, [e.target.name]: e.target.value }
		});

	onChangeValue = values => {
		this.setState({
			data: { ...this.state.data, salary: { amount: values.value } }
		});
	};

	createNewEmployee = () => {
		const errors = this.validate(this.state.data);
		this.setState({ errors });
		if (Object.keys(errors).length === 0) {
			this.setState({ loading: true, errors: {} });
			this.props
				.create(this.state.data)
				.catch(err => {
					this.setState({
						errors: { global: err.response.data.message }
					});
					this.setState({ loading: false });
				})
				.finally(() => {
					this.setState({ loading: false });
				});
		}
	};

	validate = data => {
		const errors = {};

		if (!data.name) errors.name = "It's can't be blanck";
		if (!data.salary.amount) errors.amount = "It's can't be blanck";

		return errors;
	};

	setError = error => {
		this.setState({ errors: error });
	};

	render() {
		return (
			<div>
				<Container>
					{this.state.errors.global && (
						<Alert color="danger">{this.state.errors.global}</Alert>
					)}
					{this.props.connected ? (
						<div>Connected to server</div>
					) : (
						<div>
							Connecting to server <Spinner />
						</div>
					)}
					<Card
						body
						id={1}
						outline
						color="white"
						className="text-center shadow"
					>
						{this.state.errors.name && (
							<InlineError text={this.state.errors.name} />
						)}
						<Input
							type="text"
							name="name"
							id="name"
							placeholder="Employee name"
							onChange={this.onChange}
							value={this.state.data.name}
							error={this.state.errors.name}
							disabled={this.state.loading}
						/>
						{this.state.errors.amount && (
							<InlineError text={this.state.errors.amount} />
						)}
						<NumberFormat
							className="mb-2"
							customInput={Input}
							decimalScale={0}
							allowNegative={false}
							thousandSeparator=" "
							placeholder="Type value..."
							error="wrong"
							success="right"
							id="amount"
							name="amount"
							value={this.state.data.salary.amount}
							onValueChange={this.onChangeValue}
							disabled={this.state.loading}
						/>
						<FormButton
							loading={this.state.loading}
							variant="primary"
							block
							submit={this.createNewEmployee}
						>
							Create new employee
						</FormButton>
						{this.props.hasRoleAdmin && (
							<FormButton
								loading={this.state.loading}
								variant="danger"
								block
								submit={this.props.delete}
							>
								Delete all employees
							</FormButton>
						)}
					</Card>
					<Row>
						{this.props.employees.map(em => (
							<Col md="4" className="padding-10">
								<Employee
									edit={this.props.edit}
									employee={em}
									errors={this.setError}
								/>
							</Col>
						))}
					</Row>
				</Container>
			</div>
		);
	}
}

EmployeeForm.propTypes = {
	hasRoleAdmin: PropTypes.bool.isRequired,
	delete: PropTypes.func.isRequired,
	create: PropTypes.func.isRequired,
	edit: PropTypes.func.isRequired,
	employees: PropTypes.arrayOf(PropTypes.object).isRequired,
	connected: PropTypes.bool.isRequired
};

export default EmployeeForm;
