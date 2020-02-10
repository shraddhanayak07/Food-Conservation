import React from 'react';
import {Component} from 'react';
import alertBox from "sweetalert";

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

class Donor extends Component {
	constructor(props) {
    	super(props);
    	this.state = {
      foodtype: null,
      quantity: null,
      dropofflocation: "",
      formErrors: {
        foodtype: "",
        quantity: "",
        dropofflocation: ""
      },
      createFoodData: {
      	type: "",
        quantity: "",
        location: "",
        donated_by: ""
      },
      response: {},
      overallError: ""
    };
    this.dropofflocationChange = this.dropofflocationChange.bind(this);
   }

   dropofflocationChange(event) {
   	console.log(sessionStorage.getItem('loginData'));
   	this.setState({dropofflocation: event.target.value});
  }
   	fetchItems(){

   		let items = []; 
   		fetch("http://localhost:8080/food_conservation_app/webapi/locations?city=san Jose", {
	      method: "GET"
	    })
	      .then(response => response.json())
	      .then(data => 
	        items = this.createSelectItems(data)
	      )
	      .catch(error => this.setState({ error, isLoading: false }));
   	}

    createSelectItems(data) {
     	let items = [];         
     	for (let i = 0; i <= data.locations.length; i++) {             
          items.push(<option key={i} value={i}>{i}</option>);   
          //here I will be creating my options dynamically based on
          //what props are currently passed to the parent component
     	}
    	return items;
 	} 

 	onDropdownSelected(e) {

    	console.log("THE VAL", e.target.value);
    	//here you will see the current selected value of the select input
	} 

	handleSubmit = e => {
    e.preventDefault();

    	if (formValid(this.state)) {
      		console.log(`
	        --SUBMITTING--
	        
	        `);
          let val = JSON.parse(sessionStorage.getItem("loginData"));
	        this.state.createFoodData.type = this.state.foodtype;
	        this.state.createFoodData.quantity = this.state.quantity
	        this.state.createFoodData.location =  this.state.dropofflocation
          this.state.createFoodData.donated_by = val.userID
	        
	        let m = createFood(this.state.createFoodData);
	        this.setState( {overallError,  m});
	        
    	} else {
      		this.state.overallError = "Please remove all errors in the form";
      		console.error("FORM INVALID - DISPLAY ERROR MESSAGE");
    	}
  	};

  	handleChange = e => {
	    	e.preventDefault();
	    	const { name, value } = e.target;
	    	let formErrors = { ...this.state.formErrors };
	    	let message = this.state.overallError;

	    	switch (name) {
	      		case "foodtype":
	      			this.state.overallError = "";
	        		break;
	      		case "quantity":
	      			this.state.overallError = "";
	        		formErrors.quantity =
	          		value < 1 ? "quamtity cannot be negative" : "";
	        		break;
	      		default:
	        		break;
	    	}

	    this.setState({ formErrors, [name]: value }, () => console.log(this.state));
	  };

   render(){
   		const { formErrors } = this.state;
   		const val = JSON.parse(sessionStorage.getItem('loginData'));
   		const message = this.state.overallError;
   		let first="",value1="",value2="";
   		let second = "";
   		if(val.city == "San Jose"){
   			first = "San Jose City Hall";
   			value1=1;
   			second = "Twilight Drive";
   			value2 = 2;
   			//this.state.dropofflocation=1;
   		}else{
   			first = "MLK Library";
   			value1=3;
   			second = "Pier 39";
   			value2=4;
   			//this.state.dropofflocation=3;
   		}
      sessionStorage.setItem("previous","Donor");
   		return (

			<div>
				<div className="header" style={{top:0}}>
         		 <h1 className="title">Food Conservation</h1>
         		 <p className="logout">Hi,{val.firstName} | <span><a href="/History">History</a></span> | <span><a href="/Login">Logout</a></span> </p>
        </div>		
				<div className="form-wrapper">
					<h1>Provide Food Details </h1>
						<form onSubmit={this.handleSubmit} noValidate>
							<div className="foodtype">
								<label htmlFor="foodtype">Food Type</label>
								<input type="text" 
									className={formErrors.foodtype.length > 0 ? "error" : null} 
									placeholder="Food Type" 
									type="text" 
									name="foodtype" 
									onChange={this.handleChange} 
									noValidate/>
									{formErrors.foodtype.length > 0 && (
		                				<span className="errorMessage">{formErrors.foodtype}</span>
		              				)}
							</div>
							<div className="quantity">
								<label htmlFor="quantity">Quantity</label>
								<input type="text" 
									className={formErrors.quantity.length > 0 ? "error" : null}
									placeholder="Quantity" 
									type="text" 
									name="quantity" 
									onChange={this.handleChange} 
									noValidate/>
									{formErrors.quantity.length > 0 && (
			                			<span className="errorMessage">{formErrors.quantity}</span>
              						)}
							</div>
							<div className="dropofflocation">
          						<label> Drop Off Locations </label>
						        <select value={this.state.dropofflocation} onChange={this.dropofflocationChange}>
						        	
                         			<option value={value1}>{first}</option>
                            		<option value={value2}>{second}</option>		
                          			
          						</select>
          					</div>
							<div className="createAccount">
								
							<button type="submit">Add Food</button>
          					</div>
						</form>
				</div>
			</div>
		);
   }
}

let overallError="";
let d = new Donor();
async function createFood(req) {
  try {

    console.log(req)
 
    const url = 'http://localhost:8080/food_conservation_app/webapi/food/addfood';
    const reposResponse = await fetch(url, {
	      method: "POST",
	      //mode: "no-cors",
	      body: JSON.stringify(req)
	    });
    const userRepos = await reposResponse.json();
    alertBox("Food Created Sucessfully","","success");

    if(userRepos.error != null){
    	overallError = "Error in food Creation";
    	return overallError;
    }else{
    	overallError = "Food Created Sucessfully";
    	return overallError;
    }
    console.log(userRepos);

  	} catch (error) {
    	console.log(error);
  	}
}

export default Donor;