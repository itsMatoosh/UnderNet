<?php
//Preloading the images.
$catalogDir = "res";
include ("websiteFrame.php");

websiteFrame::printHead();
websiteFrame::printTop(2);

//Connecting to MySQL
$servername = "localhost";
$username = "matoosh";
$password = "123456";
$db = "daezio";

// Create connection
$conn = new mysqli($servername, $username, $password, $db);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//Fetches pizzas from mysql.
$command = "SELECT * FROM dishes ORDER BY id";
$result = $conn->query($command);

echo '<div class="highlight">';

//Once row after another.
if($result->num_rows > 0) {
    while ($item=$result->fetch_assoc()) {
        echo '<div class="row" style="height: 16em; width: 100%;">'; //start row
        echo '<div class="col-md-9" style="height: 100%;">';//
        echo '<div style="margin: 1em 1em 1em 1em">';
        echo    '<div class="pizzaName"><p><b>' . "$row["name"]" . '</b></p></div>';
        echo    '<div class="pizzaDescription"><p>' . $row["description"] . '</p></div>';
        echo '</div>';
        echo '</div>';
        echo '<div class="col-md-3" style="height: 100%; overflow: visible; z-index: 1; position: relative;">';//
        echo '<img src="res/food/Pizza1.jpg" style="height: 100%; z-index: 2;"/>';
        echo '<div style="position: relative; bottom: 3.5em; z-index: 3; width: 106%; height: 3.5em;">
                <a href="order.php?item=' . $row["id"] . '" class="red btn text-center orderText" style="height: 100%; width: 100%;">
                    <p>Order now!</p>
                </a>
            </div>';
        echo '</div>';
        echo '</div>'; //end row
        echo '<div class="voffset2"></div>';
    }
} else {
    die("No results found!");
}
echo '</div>';

$conn->close();

websiteFrame::printFooter();