'use strict';

import { NativeModules, Platform, NativeEventEmitter } from 'react-native';
const { Linphone} = NativeModules;

const Linphones = {
	show: Linphone.show
}

export { Linphones };
export default Linphones;
