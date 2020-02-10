import React from 'react';
import ReactDOM from 'react-dom';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import App from './App';
import Login from "./Login"
import Donor from "./Donor"
import Volunteer from "./Volunteer"
import History from "./History"

class Middle extends React.Component {
	render() {
		return (
			<BrowserRouter>
				<Switch>
					<Route path="/" exact component={App}/>
				
					<Route path="/Register" component={App} />

					<Route path="/Login" component={Login} />

					<Route path="/Donor" component={Donor} />

					<Route path="/Volunteer" component={Volunteer} />

					<Route path="/History" component={History} />
				</Switch>
			</BrowserRouter>
		);
	}
}

export default Middle;

