/**
 *  ElasticThings
 *
 *  Copyright 2016 Raymund Rimando
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
import groovy.json.JsonBuilder
definition(
    name: "ElasticThings",
    namespace: "rrimando",
    author: "Raymund Rimando",
    description: "Smartthings to Elastic logger",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")



preferences {
    section("Log devices...") {
        input "temperatures", "capability.thermostat", title: "Temperatures", required:false, multiple: true
        input "contacts", "capability.contactSensor", title: "Doors open/close", required: false, multiple: true
        input "accelerations", "capability.accelerationSensor", title: "Accelerations", required: false, multiple: true
        input "motions", "capability.motionSensor", title: "Motions", required: false, multiple: true
        input "presence", "capability.presenceSensor", title: "Presence", required: false, multiple: true
        input "switches", "capability.switch", title: "Switches", required: false, multiple: true
        input "energy", "capability.energyMeter", title: "Energys", required: false, multiple: true
        input "power", "capability.powerMeter", title: "Power", required: false, multiple: true
        
    }
}

def installed() {
    //initialize()
}

def updated() {
    unsubscribe()
}

mappings {
  path("/_cat"){
    action: [
      GET: "cat"
    ]
  }
  path("/all"){
    action: [
      GET: "list"
    ]
  }
}

def getAttribs(device){
  def attr = []
  def attribs = [:]
  attribs["id"] = device.id
  attribs["name"] = device.name
  attribs["displayName"] = device.displayName
  device.supportedAttributes.each {att ->
    try {
        def value = device.currentValue(att.name)
        attr << att.name
        attribs["${att.name}"] = value
    } catch (all) {
        attribs["${att.name}"] = 'NONE'
    }
  }
  return attribs
}


def cat() {
    def resp = [:]
    
    [temperatures, contacts, accelerations, motions, presence, switches, energy, power].each { groups ->
       groups.each {
         log.debug " >> ${it}"
         log.debug "   >> ${it.name} ${it.label}"
         resp[it.label] = getAttribs(it)
       }
    }
   
    def json = new groovy.json.JsonBuilder(resp)
    return json.content
}

/*************************************************
def jsonlist2(){
    def resp = []
    def myGroovyMap = [ 
      someKey: "someValue",
      otherKey: [ whichkey: "whichKey" ]
    ]
    def json = new groovy.json.JsonBuilder(myGroovyMap)
    return [json.content]
}
def jsonlist3(){
       def builder = new groovy.json.JsonBuilder()
       def root = builder.people {
           person {
               firstName 'Guillame'
               lastName 'Laforge'
               // Named arguments are valid values for objects too
               address(
                       city: 'Paris',
                       country: 'France',
                       zip: 12345,
               )
               married true
               // a list of values
               conferences 'JavaOne', 'Gr8conf'
           }
       }
       return builder.toString()
}
def attributes() {
    def resp = []
    switches.each {
        resp << it
        resp << it.currentSwitch
        def attr = []
        it.supportedAttributes.each {att ->
            attr << att.name
        }
        resp << attr

        //resp << it.supportedAttributes
    }
    return resp
}

def list() {
    def resp = []
    switches.each {
        resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    contact.each {
        resp << [name: it.displayName, value: it.currentValue("contact")]
    }
    
    def thermo = []
    temperatures.each {
        thermo << [name: "temperature", value: it.currentValue("temperature")]
        thermo << [name: "OperatingState", value: it.currentValue("thermostatOperatingState")]
        thermo << [name: "Mode", value: it.currentValue("thermostatMode")]
        thermo << [name: "humidy", value: it.currentValue("humidity")]
    }
    resp << thermo

    energy.each {
        resp << [name: it.displayName, value: "0"]
    }
    return resp
}

def handleTemperatureEvent(evt) {
    log.info ("Temperature ${evt.name} ${evt.value}")
    //sendValue(evt) { it.toString() }
}

def handleGenericEvent(evt) {
    sendValue(evt) { it.toString() }
}

def handleThermoOpStateEvent(evt) {
  log.info ("handleThermoOpStateEvent: ${evt.name} ${evt.value}")
  //def mode = [ 
  //  "idle", 
  //  "fan only", 
  //  "vent economizer",
  //  "cooling",
  //  "pending heat",
  //  "heating",
  //  "pending cool" 
  //]
  //sendValue(evt) { it = mode[evt.value] }
}

def handleContactEvent(evt) {
    sendValue(evt) { it == "open" ? "true" : "false" }
}

def handleAccelerationEvent(evt) {
    sendValue(evt) { it == "active" ? "true" : "false" }
}

def handleMotionEvent(evt) {
    sendValue(evt) { it == "active" ? "true" : "false" }
}

def handlePresenceEvent(evt) {
    sendValue(evt) { it == "present" ? "true" : "false" }
}

def handleSwitchEvent(evt) {
    sendValue(evt) { it == "on" ? "true" : "false" }
}


private sendValue(evt, Closure convert) {
    def compId = URLEncoder.encode(evt.displayName.trim())
    def streamId = evt.name
    def value = convert(evt.value)
    
    log.debug "Logging to GroveStreams ${compId}, ${streamId} = ${value}"

	def url = "https://grovestreams.com/api/feed?api_key=${channelKey}&compId=${compId}&${streamId}=${value}"
    
    def putParams = [
        uri: url,
        body: []
    ] 
}
*/