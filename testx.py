import sqlite3

# Connect to SQLite database
conn = sqlite3.connect("heart_data.db")
cursor = conn.cursor()

# Create table if it doesn't exist
cursor.execute("""
CREATE TABLE IF NOT EXISTS heart_metrics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    rr_interval INTEGER
)
""")

# Read the .txt file and insert each RR interval
with open("rr_values.txt", "r") as file:
    for line in file:
        line = line.strip()  # Remove whitespace and newline
        if line:  # Skip empty lines
            rr_value = int(line)
            cursor.execute("INSERT INTO heart_metrics (rr_interval) VALUES (?)", (rr_value,))

# Commit changes and close connection
conn.commit()
conn.close()

print("All RR intervals have been inserted into heart_metrics!")
