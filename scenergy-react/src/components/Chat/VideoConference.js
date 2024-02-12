import {useEffect, useRef, useState} from 'react';
import Peer from 'peerjs';


const VideoConference = ({chatRoomId, chatRoomUsers}) => {
    const [peerId, setPeerId] = useState('');
    const [remotePeerIdValue, setRemotePeerIdValue] = useState('');
    const remoteVideoRef = useRef(null);
    const currentUserVideoRef = useRef(null);
    const peerInstance = useRef(null);

    useEffect(() => {
        const peer = new Peer(testId);

        peer.on('open', (peerId) => {
            setPeerId(peerId)
        });

        peer.on('call', (call) => {
            var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

            getUserMedia({video: true, audio: true}, (mediaStream) => {
                currentUserVideoRef.current.srcObject = mediaStream;
                currentUserVideoRef.current.play();
                call.answer(mediaStream)
                call.on('stream', function (remoteStream) {
                    remoteVideoRef.current.srcObject = remoteStream
                    var playPromise = remoteVideoRef.current.play();

                    if (playPromise !== undefined) {
                        playPromise.then(_ => {
                        })
                            .catch(error => {
                            });
                    }
                });
            });
        })

        peerInstance.current = peer;
    }, [])

    const call = (remotePeerId) => {
        var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

        getUserMedia({video: true, audio: true}, (mediaStream) => {

            currentUserVideoRef.current.srcObject = mediaStream;
            currentUserVideoRef.current.play();

            const call = peerInstance.current.call(remotePeerId, mediaStream)

            call.on('stream', (remoteStream) => {
                remoteVideoRef.current.srcObject = remoteStream
                var playPromise = remoteVideoRef.current.play();

                if (playPromise !== undefined) {
                    playPromise.then(_ => {
                    })
                        .catch(error => {
                        });
                }

            });
        });
    }

    return (
        <div>
            <h1>Current user id is {peerId}</h1>
            <input type="text" value={remotePeerIdValue} onChange={e => setRemotePeerIdValue(e.target.value)}/>
            <button onClick={() => call(remotePeerIdValue)}>Call</button>
            <div>
                <video ref={currentUserVideoRef}/>
            </div>
            <div>
                <video ref={remoteVideoRef}/>
            </div>
        </div>
    );
}

export default VideoConference;