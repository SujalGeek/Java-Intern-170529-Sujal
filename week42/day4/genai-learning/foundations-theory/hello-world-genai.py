print("hello to the world of the gen ai learning!!!")

import requests

response = requests.get("http://api.github.com")
print(response.status_code)

def say_goodbye():
    print("This is the function calling")
    print("This is the best thing")

say_goodbye()


def check_whether():
    temperature = 50
    if temperature > 35:
        print("It is too hot out there ")
    else:
        print("It's nice weather!!")


def greet(name):
    print(f"Hello, {name}!")

check_whether()
greet("Raj")
greet("Rohan")



def double(number):
    return number*2


result = double(4)
print("The result of the double number is",result)


# import requests

# We need coordinates to get weather data
latitude = 48.85   # Paris latitude
longitude = 2.35   # Paris longitude

# Build the API URL with our parameters
url = f"https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m"

# Make the request
response = requests.get(url)
data = response.json()

print(data)


def get_weather(latitude,longitude):
    response = requests.get(f"https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m")
    data = response.json()
    return data['current']['temperature_2m']


def get_weather(latitude,longitude):

  # latitude = 48.85   # Paris latitude
#   22.128965,73.412869

  # longitude = 2.35   # Paris longitude

# Build the API URL with our parameters
  url = f"https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m"

# Make the request
  response = requests.get(url)
  data = response.json()
  return data['current']['temperature_2m']

paris_temp = get_weather(48.85,2.35)
london_temp = get_weather(51.50,-0.2)
tokyo_temp = get_weather(35.68,139.29)
dabhoi_temp = get_weather(22.128965,73.412869)

print(f"Paris: {paris_temp}°C")
print(f"London: {london_temp}°C")
print(f"Tokyo: {tokyo_temp}°C")
print(f"Dabhoi: {dabhoi_temp}°C")


"""
The result of the double number is 8
{'latitude': 48.84, 'longitude': 2.3599997, 'generationtime_ms': 0.035762786865234375, 'utc_offset_seconds': 0, 'timezone': 'GMT', 'timezone_abbreviation': 'GMT', 'elevation': 46.0, 
'current_units': {'time': 'iso8601', 'interval': 'seconds', 'temperature_2m': '°C'}, 
'current': {'time': '2026-05-28T11:30', 'interval': 900, 'temperature_2m': 31.2}}

"""

# current



import requests
from datetime import datetime, timedelta

# Calculate dates
today = datetime.now()
week_ago = today - timedelta(days=7)

# Format dates for API (YYYY-MM-DD)
start_date = week_ago.strftime("%Y-%m-%d")
end_date = today.strftime("%Y-%m-%d")

# Get Paris weather for past week
url = f"https://api.open-meteo.com/v1/forecast?latitude=48.85&longitude=2.35&start_date={start_date}&end_date={end_date}&daily=temperature_2m_max,temperature_2m_min"

response = requests.get(url)
data = response.json()
print("This is the data from the date and time one it is not coming here!!",data)

