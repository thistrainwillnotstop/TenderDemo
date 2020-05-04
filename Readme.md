Frameworks/libs used:
- Springboot framework
- jOOQ for database access
- H2 database


Justification:

- H2: forsimplicity of this demo project sharing/running
- jOOQ: powerful data access framework which leaves you in full control, unlike most of ORMs, but still is of great help interacting with db
- JWT auth endpoint: for simplicity, JWT token generator is embedded in this server as endpoint. Normally, it should be separate server. Also JWT signing alghoritm should be RSA if possible. 

Authentication:

- Client obtains JWT token via /auth endpoint.
- /auth endpoint takes JSON in format { "username":"myUserName", "password":"myPassword"}
- response is JSON in format { "jwt":"jwt_token"}
- Client has to use received JWT token inside Authorization header in following format:
key: "Authorization" value: "Bearer jwt_token"
- JWT token holds user's ID and authorities
- Endpoints are secured so that specific authorities("ISSUER", "BIDDER") can access specific endpoints

Endpoints:

- All endpoints return BAD_REQUEST if params are invalid
- All endpoints return INTERNAL_ERROR with message for other failures
- All endpoints are POST, consume/produce JSON

1. /createTender --- Creates new tender with description
- consumes JSON {"description": "some tender description"} - description of tender
- produces JSON {"tenderId": 1} - tender id

2. /submitOffer--- Submits new offer for tender
- consumes JSON {"offerInfo": "some offer description"} - description of offer
- produces JSON {"result": success} - tender id

- Checks first to see if Tender is open, before submiting offer. Done in transaction.


3. /acceptOffer--- Issuer accepts 1 bidder's offer
- consumes JSON {"tenderOfferId": 34} - offer id
- produces JSON {"result": success} - tender id

- It accepts only one offer, rejects all other for this tender, and sets tender status to CLOSED. Done in transaction


4. /getAllTenderOffers--- Retrive all tender offers for specific tender
- consumes JSON {"tenderId": 4} - tender id
- produces JSON array [{"tender_offers_id": 1}, "offer_info": "some offer info"}]



5. /getAllTenderOffersByBidder--- Retrive all tender offers for specific tender by specific bidder
- consumes JSON {"bidderId": 1, "tenderId": 3} - description of tender
- produces JSON {"tenderId": 1} - tender id
- produces JSON array [{"tender_offers_id": 1}, "offer_info": "some offer info"}]


6. /getAllTendersByIssuer--- Creates new tender with description
- consumes nothing
- produces JSON: 
[{
"tenderId": 1,
"description": "some description",
"createdAt": "20200503 10:00:55.174",
"status": "OPEN"
}]


- submitOffer and acceptOffer are done via transactions
