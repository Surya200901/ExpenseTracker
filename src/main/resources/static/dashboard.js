document.addEventListener("DOMContentLoaded", () => {
    fetchDashboardData();
});

function fetchDashboardData() {
    fetch('/api/dashboard/overview')
        .then(response => response.json())
        .then(data => {
            document.getElementById('overview-details').innerHTML = `
                <p>Total Income: $${data.totalIncome}</p>
                <p>Total Expenses: $${data.totalExpenses}</p>
                <p>Total Savings: $${data.totalSavings}</p>
            `;
        });

    fetch('/api/dashboard/recent-transactions')
        .then(response => response.json())
        .then(data => {
            const transactionsList = document.getElementById('transactions-list');
            transactionsList.innerHTML = `
                <h3>Recent Income</h3>
                <ul>${data.recentIncome.map(income => `<li>${income.description}: $${income.amount}</li>`).join('')}</ul>
                <h3>Recent Expenses</h3>
                <ul>${data.recentExpenses.map(expense => `<li>${expense.description}: $${expense.amount}</li>`).join('')}</ul>
            `;
        });

    fetch('/api/dashboard/spending-analysis')
        .then(response => response.json())
        .then(data => {
            const analysisChart = document.getElementById('analysis-chart');
            analysisChart.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
        });
}
