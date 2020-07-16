import React, { Component } from "react";
import { StyleSheet, View, Image, PermissionsAndroid, Alert } from "react-native";

export const IMAGENAME = require('../src/ic_logo.png'); 

async function requestPermissions(props){
  try {
    const result =  await PermissionsAndroid.requestMultiple(
      [PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION],
      {
        title: "Geo Tracker Wants to access your location",
        message:
          "App Want to access your location" +
          "so your location can be store for ping data.",
        buttonNeutral: "Ask Me Later",
        buttonNegative: "Cancel",
        buttonPositive: "OK"
      }
    );
    if (result[PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION]
    && result[PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION] === 'granted') {
        props.navigation.replace('Home')
    } else {  
        Alert.alert("Please denied. Please allow manually from settings.");
    }
  } catch (err) {
    console.warn(err);
  }
};





export default class Home extends Component {
      constructor(props){
          super(props)
      }
    render() {
    return (
      <View style={styles.container}>
        <Image
          source ={IMAGENAME}
          style={{
            height: 150,
            width:150
          }}
       />
      </View>
    );
  }
  async componentDidMount(){
    await requestPermissions(this.props);
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignContent: "center",
    alignItems: "center",
    backgroundColor: "#ecf0f1",
    padding: 8
  },
  item: {
    margin: 24,
    fontSize: 18,
    fontWeight: "bold",
    textAlign: "center"
  }
});
