<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Get Transactions</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 20px;
	background-color: #f4f4f4;
}

form {
	background-color: #fff;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	margin-bottom: 20px;
	width: 35%;
}

label {
	display: block;
	margin-top: 10px;
	font-weight: bold;
}

input[type="text"], input[type="date"], button {
	width: 100%;
	padding: 10px;
	margin-top: 5px;
	border: 1px solid #ddd;
	border-radius: 4px;
	box-sizing: border-box;
}

button {
	background-color: #4CAF50;
	color: white;
	border: none;
	cursor: pointer;
	margin-top: 15px;
}

button:hover {
	background-color: #45a049;
}

#results {
	background-color: #e3e3e3;
	padding: 20px;
	border-radius: 8px;
	display: none;
	white-space: pre-wrap;
	word-wrap: break-word;
}

#results h2 {
	margin-top: 0;
}

.results-container {
	margin-top: 20px;
	display: none;
	width: 40%;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 20px;
	font-family: Arial, sans-serif;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

th, td {
	padding: 12px 16px;
	border: 1px solid #ddd;
	text-align: left;
}

th {
	background-color: #4CAF50;
	color: white;
}

tr:nth-child(even) {
	background-color: #f9f9f9;
}

tr:hover {
	background-color: #f1f1f1;
}

h2 {
	font-family: Arial, sans-serif;
	color: #333;
}
</style>
<script>
   function fetchData() {
	    const customerId = document.getElementById("customerId").value;
	    const fromDate = document.getElementById("fromDate").value;
	    const toDate = document.getElementById("toDate").value;

	    const errorMessageDiv = document.getElementById("errorMessage");
	    errorMessageDiv.innerText = "";

	    const url = "/api/rewards/customer/" + customerId + "?fromDate=" + fromDate + "&toDate=" + toDate;

	    fetch(url, {
	        method: 'GET',
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    })
	    .then(response => {
	        if (!response.ok) {
	            return response.text().then(text => { throw new Error(text) });
	        }
	        return response.json();
	    })
	    .then(data => {
	        errorMessageDiv.innerText = "";

	        document.getElementById("customerIdCell").innerText = data.customerId;
	        document.getElementById("totalPointsCell").innerText = data.totalPoints;

	        const monthlyPointsBody = document.getElementById("monthlyPointsBody");
	        monthlyPointsBody.innerHTML = "";

	        if (data.monthlyData && typeof data.monthlyData === 'object') {
	            for (const month in data.monthlyData) {
	                const row = document.createElement("tr");

	                const monthCell = document.createElement("td");
	                monthCell.textContent = month;

	                const txnCountCell = document.createElement("td");
	                txnCountCell.textContent = data.monthlyData[month].transactionCount;

	                const pointsCell = document.createElement("td");
	                pointsCell.textContent = data.monthlyData[month].points;

	                row.appendChild(monthCell);
	                row.appendChild(txnCountCell);
	                row.appendChild(pointsCell);

	                monthlyPointsBody.appendChild(row);
	            }
	        } else {
	            const row = document.createElement("tr");
	            row.innerHTML = `<td colspan="3" style="color: red;">No monthly data found</td>`;
	            monthlyPointsBody.appendChild(row);
	        }

	        document.getElementById("resultsContainer").style.display = "block";
	    })
	    .catch(error => {

	    	errorMessageDiv.innerText = error.message;

	        document.getElementById("resultsContainer").style.display = "none";
	    });
	}

</script>

</head>
<body>
	<form>
		<h2 style="text-align: center;">Know Customer Rewards</h2>
		<label for="customerId">Customer ID:<span style="color: red;">*</span></label>
		<input type="text" id="customerId" name="customerId" required><br>
		<br> <label for="fromDate">From Date:<span
			style="color: red;">*</span></label> <input type="date" id="fromDate"
			name="fromDate" required><br>
		<br> <label for="toDate">To Date:<span
			style="color: red;">*</span></label> <input type="date" id="toDate"
			name="toDate" required><br>
		<br>

		<button type="button" onclick="fetchData()">Get Transactions</button>
	</form>

	<div id="errorMessage" style="color: red; font-weight: bold;"></div>

	<div class="results-container" id="resultsContainer"
		style="display: none;">
		<h2>Customer Rewards</h2>
		<table>
			<tr>
				<th>Customer ID</th>
				<td id="customerIdCell"></td>
			</tr>
			<tr>
				<th>Total Reward Points</th>
				<td id="totalPointsCell"></td>
			</tr>
		</table>

		<h2>Monthly Rewards</h2>
		<table>
			<thead>
				<tr>
					<th>Month</th>
					<th>Transactions Count</th>
					<th>Reward Points Earned</th>
				</tr>
			</thead>
			<tbody id="monthlyPointsBody">
			</tbody>
		</table>

	</div>
</body>
</html>