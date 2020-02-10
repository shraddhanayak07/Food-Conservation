import React from 'react';
import {Component} from 'react';
import './App.css';
import foodwaste from './foodwaste.jpeg';
import axios from 'axios';
import SelectBox from './Dropdown.js';
import { BrowserRouter , Link } from 'react-router-dom';
import { Route, Router} from "react-router";
import {Login} from "./Login"
import {User} from "./User"

const emailRegex = RegExp(
  /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
);

const formValid = ({ formErrors, ...rest }) => {
  let valid = true;

  // validate form errors being empty
  Object.values(formErrors).forEach(val => {
    val.length > 0 && (valid = false);
  });

  // validate the form was filled out
  Object.values(rest).forEach(val => {
    val === null && (valid = false);
  });

  return valid;
};

let p = "";

 class App extends Component {
	constructor(props) {
    super(props);
    p = props;
    this.state = {
      firstName: null,
      lastName: null,
      email: null,
      password: null,
      confirmpassword: null,
      city: 'San Jose',
      userType: 'Donor',
      formErrors: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmpassword: ""
      },
      registerUser: {
      	firstName: "",
        lastName: "",
        email: "",
        password: "",
        userType: "",
        city: ""
      },
      response: {},
      overallError: ""
    };
    this.cityChange = this.cityChange.bind(this);
    this.userTypeChange = this.userTypeChange.bind(this);
  }

  cityChange(event) {
    this.setState({city: event.target.value});
  }

  userTypeChange(event) {
    this.setState({userType: event.target.value});
  }

	handleSubmit = e => {
    e.preventDefault();

    if (formValid(this.state)) {
      console.log(`
        --SUBMITTING--
        City: ${this.state.city}
        //Last Name: ${this.state.lastName}
        //Email: ${this.state.email}
        //Password: ${this.state.password}
        `);
        this.state.registerUser.firstName = this.state.firstName;
        this.state.registerUser.lastName = this.state.lastName
        this.state.registerUser.email =  this.state.email
        this.state.registerUser.password = this.state.password
        this.state.registerUser.city = this.state.city
        this.state.registerUser.userType = this.state.userType
        this.registerUser();
        // if(this.state.response != null){
        //     const resp = this.state.response;
        //     if(resp.userID)
        //       console.log(resp.userID);
        // }else{
        //   console.log(this.state.response);
        // }
    } else {
      this.state.overallError = "Please remove all errors in the form";
      console.error("FORM INVALID - DISPLAY ERROR MESSAGE");
    }
  };

  abc(a)  {
  	console.log(a);
  	p.history.push('/Login');
  }

  registerUser(){

  fetch("http://localhost:8080/food_conservation_app/webapi/user", {
      method: "POST",
      //mode: "cors",
      body: JSON.stringify(this.state.registerUser)
    }).then(response => {
    	if(response.ok){
    		return response.json();
    	}else{
    		throw new Error('OK');
    	}
    }).then(data => 
        // this.setState({
        //     response:data,
        // })
    	this.abc(data)
      )
      .catch(error => this.setState({ error, isLoading: false }));
    };

    
  handleChange = e => {
    e.preventDefault();
    const { name, value } = e.target;
    let formErrors = { ...this.state.formErrors };

    switch (name) {
      case "firstName":
        formErrors.firstName =
          value.length < 3 ? "minimum 3 characaters required" : "";
        break;
      case "lastName":
        formErrors.lastName =
          value.length < 3 ? "minimum 3 characaters required" : "";
        break;
      case "email":
        formErrors.email = emailRegex.test(value)
          ? ""
          : "invalid email address";
        break;
      case "password":
        formErrors.password =
          value.length < 6 ? "minimum 6 characaters required" : "";
        break;
      case "confirmpassword":
        formErrors.confirmpassword =
          (this.state.password != value) ? "does not match with password" : "";
        break;
      default:
        break;
    }

    this.setState({ formErrors, [name]: value }, () => console.log(this.state));
  };

  // render(){
  //   return (
  //     <Router>
  //       <Route path={"user"} component={User}/>
  //     </Router>
  //     );
  // }

	render(){
		const { formErrors } = this.state;

		return <div> 
		
		<div>
			<div class="header" style={{top:0}}>
  				<h1 className="title">Food Conservation</h1>
			</div>
			<div className="form-wrapper">
				<h1>Create Account</h1>
				<form onSubmit={this.handleSubmit} noValidate>
					<div className="firstName">
						<label htmlFor="firstName">First Name</label>
						<input type="text" 
						className={formErrors.firstName.length > 0 ? "error" : null} 
						placeholder="First Name" 
						type="text" 
						name="firstName" 
						onChange={this.handleChange} 
						noValidate/>
						{formErrors.firstName.length > 0 && (
                <span className="errorMessage">{formErrors.firstName}</span>
              )}
					</div>
					<div className="lastName">
						<label htmlFor="lastName">Last Name</label>
						<input type="text" 
						className={formErrors.lastName.length > 0 ? "error" : null}
						placeholder="Last Name" 
						type="text" 
						name="lastName" 
						onChange={this.handleChange} 
						noValidate/>
						{formErrors.lastName.length > 0 && (
                <span className="errorMessage">{formErrors.lastName}</span>
              )}
					</div>
					<div className="email">
						<label htmlFor="email">Email</label>
						<input type="text" 
						className={formErrors.email.length > 0 ? "error" : null}
						placeholder="Email" 
						type="text" 
						name="email" 
						onChange={this.handleChange} 
						noValidate/>
						{formErrors.email.length > 0 && (
                			<span className="errorMessage">{formErrors.email}</span>
              			)}
					</div>
					<div className="password">
						<label htmlFor="password">Password</label>
						<input type="password" 
						className={formErrors.password.length > 0 ? "error" : null}
						placeholder="Password" 
						name="password" 
						onChange={this.handleChange} 
						noValidate/>
						{formErrors.password.length > 0 && (
                <span className="errorMessage">{formErrors.password}</span>
              )}
					</div>
					<div className="confirmpassword">
						<label htmlFor="confirmpassword">Confirm Password</label>
						<input type="password" 
						className={formErrors.confirmpassword.length > 0 ? "error" : null} 
						placeholder="Confirm Password" 
						name="confirmpassword" 
						onChange={this.handleChange} 
						noValidate/>
            {formErrors.confirmpassword.length > 0 && (
                <span className="errorMessage">{formErrors.confirmpassword}</span>
              )}
					</div>
          <div className="city">
          <label> City </label>
          <select value={this.state.city} onChange={this.cityChange}>
            <option value="San Jose">San Jose</option>
            <option value="San Francisco">San Francisco</option>
          </select>
          </div>
          <div className="userType">
          <label> User Type </label>
          <select value={this.state.userType} onChange={this.userTypeChange}>
            <option value="Donor">Donor</option>
            <option value="Volunteer">Volunteer</option>
          </select>
          </div>
          <div className="createAccount">
            <button type="submit">Create Account</button>
            <a href="/Login">Already Have an Account?</a>
          </div>
				</form>
			</div>
		</div>	
		</div>
	}
}

export default App;
