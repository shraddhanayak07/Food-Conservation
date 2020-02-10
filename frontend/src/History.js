import React, { Component } from "react";
import Table from "react-bootstrap/Table";
import { Route } from "react-router-dom";
import axios from "axios";
import { Redirect } from "react-router-dom";
import './App.css';

class History extends Component {
  constructor(props) {
    super(props);
    this.state = {
      food : []
    };
  }
  componentDidMount() {
    let url = "http://localhost:8080/food_conservation_app/webapi/history?";
    url = url + "user=" + sessionStorage.getItem("userID");
     axios
      .get(url)
      .then(response => {
        console.log("response is..........", response.data);
        this.setState({
          food: response.data.food
        });
        
      })
      .catch(err => console.log(err));
  }

  render() {
    let previous_section = '';
    if(sessionStorage.getItem("previous") == "Donor"){
      previous_section = "/Donor"
    }else{
      previous_section = "/Volunteer"
    }
  
  	let details_food = this.state.food.map(food => {
      return (
        <div className="diff-form-wrapper">
          <Table bordered width="1200">
            <tr>
              <td style={item1} width="50%">
                Food Type:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.type}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Food Quantity:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.quantity}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Location:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.location_address}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Location:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.location_city}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Donated By:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.donated_by_first_name}
                {" "}
                {food.donated_by_last_name}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Distributed By:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.distributed_by_first_name}
                {" "}
                {food.distributed_by_last_name}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Donated On:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.donated_date}
              </td>
            </tr>
            <tr>
              <td style={item1} width="50%">
                Distributed On:
              </td>
              <td width="50%" style={item1}>
                {" "}
                {food.distributed_on}
              </td>
            </tr>
            
          </Table>
        </div>
      );
    });
    
    const val = JSON.parse(sessionStorage.getItem('loginData'));
  	return (
  		
      <div>
        <div className="header" style={{top:0}}>
          <h1 className="title">Food Conservation</h1>
          <p className="logout">Hi,{val.firstName} | <span><a href={previous_section}>Home</a></span> | <span><a href="/Login">Logout</a></span> </p>
        </div>
        <h1 style={{marginTop:10}}>History of your Contribution</h1>
        <div>
  			 {details_food}
        </div>
      </div>
  	)
  }
}

const group = {
  width: 320,
  height: 700
};
const para = {
  fontfamily: "Arial, Helvetica, sans-serif",
  fontSize: 30,
  textAlign: "center",
  padding: 10,
  margin: 10,
  display: "inline-block",
  verticalAllign: "middle"
};
const para1 = {
  fontfamily: "Arial, Helvetica, sans-serif",
  fontSize: 20,
  textAlign: "center",
  padding: 10,
  margin: 10,

  verticalAllign: "middle"
};
const tabletext = {
  textAlign: 'right', 
  alignSelf: 'stretch'
};
const btn = {
  color: "#bd0d39",
  fontFamily: "Arial, Helvetica, sans-serif",
  fontStyle: "normal"
};
const item1 = {
  textAlign: "left",
  fontSize: 20,
  fontWeight: "lighter",
  fontfamily: "-apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Open Sans, Helvetica Neue, sans-serif"
};
export default History;