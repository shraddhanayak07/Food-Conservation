import React from 'react';
import {Component} from 'react';
import './App.css';
import Donor from "./Donor"

const emailRegex = RegExp(
  /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
);
//const reqVar;
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

let overallError = "";
let p = "";

async function getFromGitHub(req) {
  try {
    //overallError = "";
    
    const reposResponse = await fetch("http://localhost:8080/food_conservation_app/webapi/login", {
	      method: "POST",
	      //mode: "no-cors",
	      body: JSON.stringify(req)
	    });
    const userRepos = await reposResponse.json();

    if(userRepos.error != null){
    	overallError = "Auth Error";
    }else{
    	sessionStorage.setItem('loginData',JSON.stringify(userRepos));
    	sessionStorage.setItem('userID',userRepos.userID);
    	if(userRepos.userType === 'Donor')
    		p.history.push('/Donor');
    	else
    		p.history.push('/Volunteer');
    }

    console.log(userRepos);

  } catch (error) {
    console.log(error);
  }
}

class Login extends Component{

	 constructor(props) {
	    	super(props);
	    	p = props;
		    this.state = {
		      
		      email: null,
		      password: null,
		      formErrors: {
		        email: "",
		        password: "",
		      },
		      loginUser: {
		        email: "",
		        password: ""
		      },
		      response: {},
		      overallError: ""
		    };
  	}

  	render() {
  		const { formErrors } = this.state;
		return (
			<React.Fragment>
			<div class="header">
  				<h1 className="title">Food Conservation</h1>
			</div>
			<div>
				<div className="form-wrapper">
					<h1>Login</h1>
						<form onSubmit={this.handleSubmit.bind(this)} noValidate>
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
								<input type="text" 
									className={formErrors.password.length > 0 ? "error" : null}
									placeholder="Password" 
									type="password" 
									name="password" 
									onChange={this.handleChange} 
									noValidate/>
									{formErrors.password.length > 0 && (
			                			<span className="errorMessage">{formErrors.password}</span>
              						)}
							</div>
							<div className="createAccount">
								<button type="submit">Login</button>
								<a href="/Register">Create Account</a>
          					</div>
						</form>
				</div>
			</div>
			</React.Fragment>
		);
	 }
	abc(a)  {
		new Donor();
  	console.log(a);
  }
	async loginUser(){
  
  		fetch("http://localhost:8080/food_conservation_app/webapi/login", {
	      method: "POST",
	      mode: "no-cors",
	      body: JSON.stringify(this.state.loginUser)
	    })
	      .then(response => response.json())
	      .then(data => 
	        this.abc(data)
	      )
	      .catch(error => this.setState({ error, isLoading: false }));
	};

	handleSubmit = e => {
    	e.preventDefault();

    	if (formValid(this.state)) {
      		console.log(`
        	--SUBMITTING--
        	`);
        
        	this.state.loginUser.email =  this.state.email
        	this.state.loginUser.password = this.state.password
       		//reqVar = this.state.loginUser;
       		getFromGitHub(this.state.loginUser);

       		if(overallError.length != 0){
       			console.error("Authentication Failed");
       		}
        	else{
        			
        	}
  		}else{
  			console.error("FORM INVALID - DISPLAY ERROR MESSAGE");
  		}
  	};

	  	handleChange = e => {
	    	e.preventDefault();
	    	const { name, value } = e.target;
	    	let formErrors = { ...this.state.formErrors };

	    	switch (name) {
	      		case "email":
	        		formErrors.email = emailRegex.test(value)
	          		? ""
	          		: "invalid email address";
	        		break;
	      		case "password":
	        		formErrors.password =
	          		value.length < 6 ? "minimum 6 characaters required" : "";
	        		break;
	      		default:
	        		break;
	    	}

	    this.setState({ formErrors, [name]: value }, () => console.log(this.state));
	  };
}

export default Login;