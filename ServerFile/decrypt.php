<?php
// File: decrypt.php
// Copyright (c) 2026 Steve Curtis, Six Actual Studios
// All rights reserved.
// 
// This code is proprietary and confidential.
//
// This is the server that lives on sixactualstudios.com.
// Agents send it encrypted text and it sends back the real message.
// No database needed -- this is all math, no storage.

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: text/plain");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo "ERROR: Only POST requests are allowed.";
    exit();
}

// Read the raw text sent from the client
$input = file_get_contents("php://input");

// Make sure they actually sent something
if (empty($input)) {
    http_response_code(400);
    echo "ERROR: No text was received.";
    exit();
}

// Check for the shutdown command
// Since this is a web server (not a socket server), we just acknowledge it.
if (trim($input) === "shutdown") {
    echo "SHUTDOWN_ACK";
    exit();
}

// Decrypt and send back the result
echo decryptMessage($input);
exit();

// decryptMessage()
function decryptMessage($encryptedText) {
    $result = "";

    // Go through the string one character at a time
    for ($i = 0; $i < strlen($encryptedText); $i++) {
        // Get the ASCII number of this character
        $charCode = ord($encryptedText[$i]);

        // Add 10 to reverse the encryption
        $decryptedCharCode = $charCode + 10;

        // Turn the number back into a letter and add it to our result
        $result .= chr($decryptedCharCode);
    }

    return $result;
}
?>
