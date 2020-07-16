import React, { Component } from "react";
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import Home from './components/Home';
import Dashboard from './components/Dashboard';
import MapsActivity from './components/MapsActivity';
import 'react-native-gesture-handler';
import { YellowBox } from 'react-native';

YellowBox.ignoreWarnings([
  'Async Storage has been extracted from react-native core and will be removed in a future release.',
]);


const Stack = createStackNavigator();

function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator>
      <Stack.Screen name="Permisson" component={Home}  options={{headerShown:false}} />
      <Stack.Screen name="Home" component={Dashboard}/>
      <Stack.Screen name="Maps" component={MapsActivity}  options={{headerShown:false}}/> 
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default App;