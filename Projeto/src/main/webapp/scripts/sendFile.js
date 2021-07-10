//You need to first get the file from the input, like so:
/*
 file = event.target.files[0];
 fileData = new Blob([file]);
 */
//The fileData is going to be passed to the sendFile function, along with the desired URL.
//Then wait for the promise and the file will be sent async


function getBuffer(fileData) {
    return function (resolve) {
        var reader = new FileReader();
        reader.readAsArrayBuffer(fileData);
        reader.onload = function () {
            var arrayBuffer = reader.result
            var bytes = new Uint8Array(arrayBuffer);
            resolve(bytes);
        }
    }
}

function sendFile(URL, file) {
    console.log("Trying to send file to " + URL);
    var promise = new Promise(getBuffer(file));
    // Wait for promise to be resolved, or log error.
    promise.then(function (data) {
        // Here you can pass the bytes to another function.
        console.log(data);

        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("PUT", URL, true);
        xmlhttp.onload = function (oEvent) {
            // Uploaded.
            console.log("Request status: " + xmlhttp.status);
        };
        xmlhttp.send(data);


    }).catch(function (err) {
        console.log('Error: ', err);
    });
}