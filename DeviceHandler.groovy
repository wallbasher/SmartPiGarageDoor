/**
 *  Z-Wave Garage Door Opener
 *
 *  Copyright 2014 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Simulated Garage Door Opener", namespace: "smartthings/testing", author: "SmartThings") {
		capability "Actuator"
		capability "Door Control"
        capability "Garage Door Control"
		capability "Contact Sensor"
		capability "Refresh"
		capability "Sensor"
        capability "Switch"
	}

	simulator {
		
	}

	tiles {
		standardTile("toggle", "device.door", width: 2, height: 2) {
			state("closed", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#00A0DC", nextState:"opening")
			state("open", label:'${name}', action:"door control.close", icon:"st.doors.garage.garage-open", backgroundColor:"#e86d13", nextState:"closing")
			state("opening", label:'${name}', icon:"st.doors.garage.garage-closed", backgroundColor:"#e86d13")
			state("closing", label:'${name}', icon:"st.doors.garage.garage-open", backgroundColor:"#00A0DC")
			
		}
		standardTile("open", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'open', action:"door control.open", icon:"st.doors.garage.garage-opening"
		}
		standardTile("close", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'close', action:"door control.close", icon:"st.doors.garage.garage-closing"
		}

		main "toggle"
		details(["toggle", "open", "close"])
	}
}

def parse(String description) {
	log.trace "parse($description)"
}

def open() {
	log.trace device.currentValue("door")
	if (device.currentValue("door")==null 
    	|| device.currentValue("door") == "closed") {
		sendEvent(name: "door", value: "opening")
    	sendEvent(name: "switch", value: device.deviceNetworkId + ".on")    
    	runIn(15, finishOpening)
    }
    else {
    	log.debug "Garage Door is not closed: ignore open()"
    }
}

def close() {
	log.trace device.currentValue("door")
	if (device.currentValue("door")==null 
    	|| device.currentValue("door") == "open") {
    	sendEvent(name: "door", value: "closing")
		sendEvent(name: "switch", value: device.deviceNetworkId + ".off")
		runIn(15, finishClosing)
	}
    else {
    	log.debug "Garage Door is not open: ignore close()"
    }
}

def on() {
	open()
}

def off() {
	close()
}

def finishOpening() {
    sendEvent(name: "door", value: "open")
    sendEvent(name: "contact", value: "open")
    sendEvent(name: "switch", value: "on");
}

def finishClosing() {
    sendEvent(name: "door", value: "closed")
    sendEvent(name: "contact", value: "closed")
    sendEvent(name: "switch", value: "off")
}

def refresh() {

}


def changeDoorState(newState) {
	log.trace "Received update that this Garage Door is now $newState"
    if(device.currentValue("door")=="closing" || device.currentValue("door")=="opening"){
    	log.trace "Wait till door is no longer in motion"
    }
    else {
        sendEvent(name: "door", value: newState)
        if(newState=="open" || newState=="closed") {
            sendEvent(name: "contact", value: newState)
        }
    }
}
