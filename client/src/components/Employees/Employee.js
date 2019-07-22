import React from "react";
import PropTypes from "prop-types";
import { Card, CardTitle, Label, Button, Row, Col } from "reactstrap";
import NumberFormat from "react-number-format";
import FormButton from "../commons/FormButton";

class Employee extends React.Component {
	state = {
		edite: false,
		newEmployee: {
			employeeId: null,
			salary: {
				salaryId: null,
				amount: null
			}
		}
	};

	constructor(props) {
		super(props);

		this.inputForAmount = null;
	}

	componentDidUpdate(prevProps, prevState) {
		if (!prevState.edite && this.state.edite) {
			this.focus();
		}
	}

	focus = () => {
		this.inputForAmount.focus();
	};

	handleFocus = event => {
		event.target.select();
	};

	toggleEdit = () => {
		this.setState({
			edite: true
		});
	};

	decline = () => {
		this.setState({
			newEmployee: {
				employeeId: null,
				salary: {
					salaryId: null,
					amount: null
				}
			}
		});
		this.setState({
			edite: false
		});
	};

	accept = () => {
		this.setState({ loading: true });
		this.props
			.edit(this.state.newEmployee)
			.catch(err => {
				this.props.errors({ global: err.response.data.message });
			})
			.finally(() =>
				this.setState({
					loading: false,
					newEmployee: {
						employeeId: null,
						salary: {
							salaryId: null,
							amount: null
						}
					},
					edite: false
				})
			);
	};

	onChangeAmount = values =>
		this.setState({
			newEmployee: {
				employeeId: this.props.employee.employeeId,
				name: this.props.employee.name,
				salary: {
					amount: values.value ? values.value : "",
					salaryId: this.props.employee.salary.salaryId
				}
			}
		});

	render() {
		return (
			<div>
				<Card
					body
					key={this.props.employee.employeeId}
					outline
					color="white"
					className="text-center shadow"
				>
					<CardTitle tag="h2">{this.props.employee.name}</CardTitle>
					{this.props.employee.salary && (
						<CardTitle>
							<Label>Amount</Label>
							<NumberFormat
								getInputRef={el => {
									this.inputForAmount = el;
								}}
								decimalScale={0}
								allowNegative={false}
								thousandSeparator=" "
								error="wrong"
								success="right"
								id="amountOfEmployee"
								name="amountOfEmployee"
								className="border-0 no-shadow form-input form-control input-in-header text-center"
								value={
									this.state.edite === true
										? this.state.newEmployee.salary.amount
										: this.props.employee.salary.amount
								}
								onFocus={this.handleFocus}
								onValueChange={this.onChangeAmount}
								disabled={this.state.loading}
							/>
						</CardTitle>
					)}
					{this.state.edite ? (
						<Row>
							<Col xs="6">
								<FormButton
									loading={this.state.loading}
									variant="success"
									block
									size="lg"
									submit={this.accept}
								>
									Accept
								</FormButton>
							</Col>
							<Col xs="6">
								<Button
									size="lg"
									block
									color="danger"
									onClick={this.decline}
								>
									Decline
								</Button>
							</Col>
						</Row>
					) : (
						<Button color="success" onClick={this.toggleEdit}>
							Change amount
						</Button>
					)}
				</Card>
			</div>
		);
	}
}

Employee.propTypes = {
	edit: PropTypes.func.isRequired,
	errors: PropTypes.func.isRequired,
	employee: PropTypes.shape({
		employeeId: PropTypes.string.isRequired,
		name: PropTypes.string.isRequired,
		salary: PropTypes.shape({
			salaryId: PropTypes.node.isRequired,
			amount: PropTypes.node.isRequired
		}).isRequired
	}).isRequired
};

export default Employee;
