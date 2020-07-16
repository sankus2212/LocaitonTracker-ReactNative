import React, { Component } from "react";
import {View, StyleSheet, PermissionsAndroid, Alert , Dimensions, TouchableOpacity, Text, AsyncStorage, AppState } from "react-native";
import MapView, { PROVIDER_GOOGLE, AnimatedRegion  } from 'react-native-maps'; 
import Geolocation from "@react-native-community/geolocation";
import { NativeModules } from 'react-native';

const styles = StyleSheet.create({
  map: {
    ...StyleSheet.absoluteFillObject,
  },
 });


let { width, height } = Dimensions.get('window');
const ASPECT_RATIO = width / height;
const LATITUDE = 0;
const LONGITUDE = 0;
const LATITUDE_DELTA = 0.0922;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

navigator.geolocation = require('@react-native-community/geolocation');
export default class MapsActivty extends Component {
  constructor(props){
    super(props)
    this.state = {
      region: {
        latitude: LATITUDE,
        longitude: LONGITUDE,
        latitudeDelta: LATITUDE_DELTA,
        longitudeDelta: LONGITUDE_DELTA,
      }
    };
  }
    render() {
        return (
          <View style = {{flex:1, flexDirection: 'column-reverse'}}>
            <MapView
                style = {styles.map}
                region={this.state.region}
                followUserLocation={true}
                ref={ref => (this.mapView = ref)}
                zoomEnabled={true}
                showsUserLocation={true}
                onMapReady={this.goToInitialLocation.bind(this)}
                initialRegion={this.state.region} >
              </MapView>
              <View style={{flexDirection:'column-reverse'}}>
                <TouchableOpacity 
                   onPress = {() => this.startWorkManager()}
                   style={{
                        height: 50,
                        margin: 15,
                        borderRadius: 50,
                        backgroundColor: "#ffffff",
                        shadowColor: '#000',
                        shadowOffset: { width: 0, height: 2 },
                        shadowOpacity: 0.5,
                        shadowRadius: 2,
                        elevation: 2,
                        alignItems: 'center',
                        alignContent: 'center',
                        justifyContent: 'center',
                        backgroundColor: 'red'}}>
                        <Text style={{fontSize : 18, color:"white"}}>Set Geo Fence</Text>    
                </TouchableOpacity>
              </View >
           
            </View>
        
        );
        
    }

    startWorkManager(){
      NativeModules.WorkManagerModule.startWorkManager(
          (data) => {
            this.props.navigation.replace('Home');
            Alert.alert("Success : "+data);
          },
          (error) =>{
              Alert.alert("Error : "+ error);
          }
      );
    }

    async componentDidMount(){
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
           Geolocation.getCurrentPosition(
            position => {
              this.setState({
                region: {
                  latitude: position.coords.latitude,
                  longitude: position.coords.longitude,
                  latitudeDelta: LATITUDE_DELTA,
                  longitudeDelta: LONGITUDE_DELTA,
                }
              });
            
            },
          (error) => console.log(error.message),
          { enableHighAccuracy: false, timeout: 10000, maximumAge: 3600000  },
          );
          this.watchID = navigator.geolocation.watchPosition(
            position => {
              this.setState({
                region: {
                  latitude: position.coords.latitude,
                  longitude: position.coords.longitude,
                  latitudeDelta: LATITUDE_DELTA,
                  longitudeDelta: LONGITUDE_DELTA,
                }
              });
              
            }
          );
        } else {  
            Alert.alert("Please denied. Please allow manually from settings.");
        }
      } catch (err) {
        console.warn(err);
      }
    }

    componentWillUnmount(){
      Geolocation.clearWatch();
    }
  
    goToInitialLocation() {
      let initialRegion = Object.assign({}, this.state.region);
      this.mapView.animateToRegion(initialRegion, 2000);
    }
}