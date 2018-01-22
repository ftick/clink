# Clink
Have friends that constantly owe you money but never seem to have cash on hand? Clink your phones together to easily transfer money without the hassle of trading emails/passwords!

# Inspiration
I pay by card most of the time, so I never have cash on me. It's convenient to have less in my wallet, but paying people in person becomes a chore, whether it's splitting a pizza, sharing a grocery bill, or tipping a street performer. At UofTHacks, Interac temporarily opened up their Money Transfer API to see how seemless money transfer technology could be applied if it was placed in the hands of individuals. We took that to heart and took on their challenge.

# Struggles
Establishing a low-power connection between two phones was tougher than we expected, but on the way to closing in on NFC, we learned about a variety of different connection protocols that phones use on a regular basis. Getting my hands dirty with a beta API presented a fresh challenge

# What I learned
For the first 12 hours of the event, I familiarized myself with Interac's API and made a mockup Java interface for our app to communicate with. After some discussion, we decided to focus on the quick in-person payment experience. That meant that I needed to convert the API integration from synchronous Java to asynchronous Android. I came out of the event gaining much more knowledge about the Android ecosystem.

# Next Steps:
- Add group payments (automatically split a bill based on number of connected phones)
- Implement biometrics for theft prevention
- Integrate popular cryptocurrencies
