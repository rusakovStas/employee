import Stomp from "stompjs";
import SockJS from "sockjs-client";
import BASE_URL from "./constants";

export default {
	stompClient: {
		onEvent: (onConnect, event, callback, onError) => {
			const socket = new SockJS(`http://${BASE_URL}/employees-app`);
			const stompClient = Stomp.over(socket);
			stompClient.connect(
				{},
				() => {
					onConnect();
					stompClient.subscribe(event, response => {
						callback(JSON.parse(response.body));
					});
				},
				() => onError()
			);
		}
	}
};
