
import React, { Component } from "react";
import {View, TouchableOpacity, Image, Alert, StyleSheet, SafeAreaView, FlatList, Text} from "react-native";
import { NativeModules } from 'react-native';

export const IMAGENAME = require('../src/ic_add.png'); 

function Item({ title }) {
    return (
      <View style={styles.item}>
        <Text style={styles.title}>{title}</Text>
      </View>
    );
  }


export default class Dashboard extends Component{
    constructor(props){
        super(props)
        this.state = {
            isWorkerStarted : false,
            listData : {
                distance: 0,
                lat: 0.00,
                lng: 0.00,
                date: null,              
                batteryPercentage: null, 
                isInOptimizedMode: null, 
                isNetworkConnected: null,
                isGPSon: null,           
                deviceModel: null     
            }
        }
    }

   getData(){
        NativeModules.SharePrefModule.getLocationData(
            (data) => {
                    if(data!=null){
                        this.setState({
                            isWorkerStarted: true,
                            listData : JSON.parse(data)
                        });
                    }else{
                        this.setState({
                            isWorkerStarted: false,
                            listData : JSON.parse(data)
                        });
                    }
                },
                
                (error) =>{
                  Alert.alert("Error : "+ error);
              } 
        );   
    }
    


    render(){
        return (
            <View style={{flex: 1, flexDirection: 'column'}}>
            <SafeAreaView style={{flex:1}}>
                <FlatList
                    data={this.state.listData}
                    renderItem={({ item }) => <Item title={"["+item.distance+" Meters] ["+item.date+"] [Lat:"+item.lat+", Lng: "+item.lng+"] ["+item.batteryPercentage+"] ["+item.isInOptimizedMode+"] ["+item.isNetworkConnected+"] ["+item.isGPSon+"] ["+item.deviceModel+"]"}  />}
                    keyExtractor={item => item.latitude}
                />
             </SafeAreaView>
            <View 
            style={{flexDirection:'row-reverse'}}>
                <TouchableOpacity 
                    style={[styles.logo, this.state.isWorkerStarted ? styles.hidden : {}]}
                    onPress={() => this.props.navigation.replace('Maps')}>   
                          <Image
                            style={[styles.image, this.state.isWorkerStarted ? styles.hidden : {}]}
                            source ={IMAGENAME}/>
                        
                </TouchableOpacity>
            </View >
        </View>
        );
    }

    

      componentDidMount(){
        this.getData();
    }

}
var styles = StyleSheet.create({
    logo: {
        height: 60,
        width: 60,
        margin: 15,
        borderRadius: 50,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.5,
        shadowRadius: 2,
        elevation: 2,
        alignItems: 'center',
        alignContent: 'center',
        justifyContent: 'center',
        backgroundColor: 'red'
    },
    hidden: {
      width: 0,
      height: 0,
    },
    image:{
        width: 20,
        height: 20,
        borderRadius:20,
        backgroundColor:20
    },
    item: {
        backgroundColor: '#f9c2ff',
        padding: 10,
        marginVertical: 4,
        marginHorizontal: 5,
        shadowColor: '#f9c2ff',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.5,
        shadowRadius: 2,  
        elevation: 2
     },
      title: {
        fontSize: 14,
      },
});
  