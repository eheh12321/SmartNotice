// set up basic variables for app

const header = document.querySelector('#recorderTitle');
const record = document.querySelector('#record');
const stop = document.querySelector('#stop');
const soundClips = document.querySelector('.sound-clips');
const canvas = document.querySelector('.visualizer');
const mainSection = document.querySelector('.main-controls');
const testPlay = document.querySelector("#testAnnounce");
const save = document.querySelector("#makeAnnounce");
const formObj = document.querySelector("form[role='form']");

// disable stop button while not recording

stop.disabled = true;

// visualiser setup - create web audio api context and canvas

let audioCtx;
const canvasCtx = canvas.getContext("2d");

//main block for doing the audio recording

if (navigator.mediaDevices.getUserMedia) {
    console.log('getUserMedia supported.');

    const constraints = { audio: true };
    let chunks = [];
    let blob;

    let onSuccess = function(stream) {
        const mediaRecorder = new MediaRecorder(stream);

        visualize(stream);

        record.onclick = function() {
            mediaRecorder.start();
            console.log(mediaRecorder.state);
            console.log("recorder started");
            record.style.background = "gray";

            stop.disabled = false;
            record.disabled = true;
            header.textContent = "녹음 진행중";
        }

        stop.onclick = function() {
            mediaRecorder.stop();
            console.log(mediaRecorder.state);
            console.log("recorder stopped");
            record.style.background = "";
            record.style.color = "";

            stop.disabled = true;
            record.disabled = false;
            header.textContent = "녹음기";
        }

        testPlay.onclick = function(e) {
            soundClips.innerHTML = "";

            const clipName = uuidv4() + ".mp3";
            const clipContainer = document.createElement('article');
            const clipLabel = document.createElement('p');
            const audio = document.createElement('audio');

            clipContainer.classList.add('clip');
            audio.setAttribute('controls', '');
            audio.setAttribute('autoplay', '');
            clipLabel.textContent = clipName;

            clipContainer.appendChild(clipLabel);
            clipContainer.appendChild(audio);
            soundClips.appendChild(clipContainer);

            audio.controls = true;
            blob = new Blob(chunks, { 'type' : 'audio/mp3;' });
            chunks = [];
            const audioURL = window.URL.createObjectURL(blob);
            console.log(blob);
            console.log(audioURL);
            audio.src = audioURL;
            console.log("recorder stopped");
        }

        // save.onclick = function (e) {
        //
        //     if(blob == null) {
        //         blob = new Blob(chunks, { 'type' : 'audio/mp3;' });
        //         chunks = [];
        //     }
        //     console.log(blob);
        //     console.log(blobToBase64(blob));
        //
        //     blobToBase64(blob).then(result => {
        //         console.log(result.toString().replace("data:audio/mp3;;base64,", ''));
        //         var input = document.createElement('input');
        //         input.type = 'hidden';
        //         input.name = 'data';
        //         input.value = result.toString().replace("data:audio/mp3;;base64,", '');
        //         formObj.appendChild(input);
        //
        //     });
        // }

        mediaRecorder.ondataavailable = function(e) {
            chunks.push(e.data);
        }
    }

    let onError = function(err) {
        console.log('The following error occured: ' + err);
    }

    navigator.mediaDevices.getUserMedia(constraints).then(onSuccess, onError);

} else {
    console.log('getUserMedia not supported on your browser!');
}
function uuidv4() {
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
}

function visualize(stream) {
    if(!audioCtx) {
        audioCtx = new AudioContext();
    }

    const source = audioCtx.createMediaStreamSource(stream);

    const analyser = audioCtx.createAnalyser();
    analyser.fftSize = 2048;
    const bufferLength = analyser.frequencyBinCount;
    const dataArray = new Uint8Array(bufferLength);

    source.connect(analyser);
    //analyser.connect(audioCtx.destination);

    draw()

    function draw() {
        const WIDTH = canvas.width
        const HEIGHT = canvas.height;

        requestAnimationFrame(draw);

        analyser.getByteTimeDomainData(dataArray);

        canvasCtx.fillStyle = 'rgb(200, 200, 200)';
        canvasCtx.fillRect(0, 0, WIDTH, HEIGHT);

        canvasCtx.lineWidth = 2;
        canvasCtx.strokeStyle = 'rgb(0, 0, 0)';

        canvasCtx.beginPath();

        let sliceWidth = WIDTH * 1.0 / bufferLength;
        let x = 0;


        for(let i = 0; i < bufferLength; i++) {

            let v = dataArray[i] / 128.0;
            let y = v * HEIGHT/2;

            if(i === 0) {
                canvasCtx.moveTo(x, y);
            } else {
                canvasCtx.lineTo(x, y);
            }

            x += sliceWidth;
        }

        canvasCtx.lineTo(canvas.width, canvas.height/2);
        canvasCtx.stroke();

    }
}

window.onresize = function() {
    canvas.width = mainSection.offsetWidth;
}

window.onresize();