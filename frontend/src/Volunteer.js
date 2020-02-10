import React from 'react';
import {Component} from 'react';
import './App.css';
import axios from "axios";

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

// let location1=[];
// let location2=[];
// let location3=[];
// let location4=[];
let overallError="";
let i=1;

class Volunteer extends Component {
  constructor(props) {
      super(props);
      this.state = {
      food: [],
      foodtype: null,
      quantity: null,
      dropofflocation: "1",
      location1:[],
      location2:[],
      location3:[],
      location4:[],
      formErrors: {
        foodtype: "",
        quantity: "",
        dropofflocation: ""
      },
      createFoodData: {
        type: "",
        quantity: "",
        location: ""
      },
      response: {},
      overallError: ""
    };
    this.dropofflocationChange = this.dropofflocationChange.bind(this);
    //getFood(this.state.createFoodData);
    //this.render();
   }

   componentDidMount() {
      sessionStorage.setItem("previous","Volunteer");
      let url = "http://localhost:8080/food_conservation_app/webapi/food";
      let food = [];
      //for(i=1;i <= 4;i++){
        //let str = "?location="+i;
        axios
        .get(url)
        .then(response => {
          console.log("response is..........", response.data.food);
          food = response.data.food;
          this.setState({
            food: response.data.food
          });
          // food.map(function(f,j){
          //   let jc = {};
          //   jc = '{"type":"'+f.type+'","qty":"'+f.quantity+'"}';
          //   if(f.location == "1"){
          //     location1.push(jc);
          //   }
          //   if(f.location == "2"){
          //     location2.push(jc);
          //   }
          //   if(f.location == "3"){
          //     location3.push(jc);
          //   }
          //   if(f.location == "4"){
          //     location4.push(jc);
          //   }
          // });
          // if(i==1){
          //   food.map(function(f, j){
          //     let jc = {};
          //     jc = '{"type":"'+f.type+'","qty":"'+f.quantity+'"}';
          //     this.state.location1[j]=jc;
          //   })
          // }
          // if(i==2){
          //   food.map(function(f, j){
          //     let jc = {};
          //     jc = '{"type":"'+f.type+'","qty":"'+f.quantity+'"}';
          //     this.state.location2[j]=jc;
          //   })
          // }
          // if(i==3){
          //   food.map(function(f, j){
          //     let jc = {};
          //     jc = '{"type":"'+f.type+'","qty":"'+f.quantity+'"}';
          //     this.state.location3[j]=jc;
          //   })
          // }
          // if(i==4){
          //   food.map(function(f, j){
          //     let jc = {};
          //     jc = '{"type":"'+f.type+'","qty":"'+f.quantity+'"}';
          //     this.state.location4[j]=jc;
          //   })
          // }
        })
        .catch(err => console.log(err));
      //}
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
          this.state.createFoodData.type = this.state.foodtype;
          this.state.createFoodData.quantity = this.state.quantity
          this.state.createFoodData.location =  this.state.dropofflocation
          
//          getFood(this.state.createFoodData);
          
      } else {
          this.state.overallError = "Please remove all errors in the form";
          console.error("FORM INVALID - DISPLAY ERROR MESSAGE");
      }
    };

    handleChange = e => {
        e.preventDefault();
        const { name, value } = e.target;
        let formErrors = { ...this.state.formErrors };

        switch (name) {
            case "foodtype":
              break;
            case "quantity":
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
      const s = this.handleInput;
      const val = JSON.parse(sessionStorage.getItem('loginData'));

      this.state.food.map(food => {
        if(food.location == "1"){
          let jc = {};
          jc = '{"type":"'+food.type+'","qty":"'+food.quantity+'"}';
          this.state.location1.push(jc)
        }
        if(food.location == "2"){
          let jc = {};
          jc = '{"type":"'+food.type+'","qty":"'+food.quantity+'"}';
          this.state.location2.push(jc)
        }
        if(food.location == "3"){
          let jc = {};
          jc = '{"type":"'+food.type+'","qty":"'+food.quantity+'"}';
          this.state.location3.push(jc)
        }
        if(food.location == "4"){
          let jc = {};
          jc = '{"type":"'+food.type+'","qty":"'+food.quantity+'"}';
          this.state.location4.push(jc)
        }
      })
      return (
      <div>
        <div className="header" style={{top:0}}>
          <h1 className="title">Food Conservation</h1>
          <p className="logout">Hi, {val.firstName} | <span><a href="/History">History</a></span> | <span><a href="/Login">Logout</a></span> </p>
        </div>
        <h1 style={{marginTop:10}}>List of Food by Location </h1>
        <div className="diff-form-wrapper">
          
            <form noValidate>
              <div className="foodlist">
                <label style={labelheading} htmlFor="foodtype">San Jose City Hall</label>
                <table>
                
                <tr>
                <th>Food Type</th>
                <th>Quantity</th>
                </tr>
                
                {this.state.location1.map(function(movie, i) {
                  let j = JSON.parse(movie);
                     return (
                      <React.Fragment>
                      
                      <tr>
                      <td key={'movie-'+ i}>{j.type}</td>
                      <td key={'movie-'+ i}>{j.qty}</td>
                      <td>
                        <div className="createAccount">
                          <button type="submit" value={'1'+j.type} onClick={s}>Done</button>
                        </div>
                      </td>
                      </tr>
                      
                      </React.Fragment>
                      )
                })}
                
                </table>
              </div>
              
            </form>
        </div>
        <div className="diff-form-wrapper">
          
            <form noValidate>
              <div className="foodlist">
                <label style={labelheading} htmlFor="foodtype">Twilight Drive</label>
                <table>
                
                <tr>
                <th>Food Type</th>
                <th>Quantity</th>
                </tr>
                
                {this.state.location2.map(function(movie, i) {
                  let j = JSON.parse(movie);
                     return (
                      <React.Fragment>
                      
                      <tr>
                      <td key={'movie-'+ i}>{j.type}</td>
                      <td key={'movie-'+ i}>{j.qty}</td>
                      <td>
                        <div className="createAccount">
                          <button type="submit" value={'2'+j.type} onClick={s}>Done</button>
                        </div>
                      </td>
                      </tr>
                      
                      </React.Fragment>
                      )
                })}
                
                </table>
              </div>
             
            </form>
        </div>
        <div className="diff-form-wrapper">
          
            <form noValidate>
              <div className="foodlist">
                <label style={labelheading} htmlFor="foodtype">MLK Library</label>
                <table>
                
                <tr>
                <th>Food Type</th>
                <th>Quantity</th>
                </tr>
                
                {this.state.location3.map(function(movie, i) {
                  let j = JSON.parse(movie);
                     return (
                      <React.Fragment>
                      
                      <tr>
                      <td key={'movie-'+ i}>{j.type}</td>
                      <td key={'movie-'+ i}>{j.qty}</td>
                      <td>
                        <div className="createAccount">
                          <button type="submit" value={'3'+j.type} onClick={s}>Done</button>
                        </div>
                      </td>
                      </tr>
                      
                      </React.Fragment>
                      )
                })}
                
                </table>
              </div>
              
            </form>
        </div>
        <div className="diff-form-wrapper">
          
            <form noValidate>
              <div className="foodlist">
                <label style={labelheading} htmlFor="foodtype">Pier 39</label>
                <table>
                
                <tr>
                <th>Food Type</th>
                <th>Quantity</th>
                </tr>
                
                {this.state.location4.map(function(movie, i) {
                  let j = JSON.parse(movie);
                     return (
                      <React.Fragment>
                      
                      <tr>
                      <td key={'movie-'+ i}>{j.type}</td>
                      <td key={'movie-'+ i}>{j.qty}</td>
                      <td>
                        <div className="createAccount">
                          <button type="submit" value={'4'+j.type} onClick={s}>Done</button>
                        </div>
                      </td>
                      </tr>
                      
                      </React.Fragment>
                      )
                })}
                
                </table>
              </div>

            </form>
        </div>
      </div>
    );
   }
   handleInput =e => {
      e.preventDefault();
      let url = "http://localhost:8080/food_conservation_app/webapi/food/deleteFood";
      let del = e.target.value;
      let pos = del.charAt(0);
      del = del.substring(1);
      const headers = {
        'Content-Type': 'application/json'
      }
      let data = {
        location: pos,
        type: del,
        distributed_by: sessionStorage.getItem("userID")
      }
      console.log("Before: "+JSON.stringify(data));
      data = JSON.stringify(data);
      axios
      .post(url,data,{headers:headers})
      .then(response => {
        console.log("response is..........", response.data);
        window.location.reload(false);
        
      })
      .catch(err => console.log(err));
    };
}

const labelheading = {
  fontSize: 20
};

export default Volunteer;