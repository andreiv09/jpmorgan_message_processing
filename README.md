Author: Andrei Vrincianu <andrei.vrincianu@gmail.com>


How to build and run:

Step into the project directory and run:

mvn package

java -jar target/jpm-1.0.jar < test_sale_1.txt
java -jar target/jpm-1.0.jar < test_sale_2.txt


Sale message format:

Message type 1: [product] at [price]
Message type 2: [number] sales of [product] at [price]
Message type 3: {add|subtract} [price] [product]
                multiply [number] [product]

Any other messages are considered errors.
[product] = a string, may include multiple words, punctuation
[price]   = nn.nn (expressed in GBP) or nnp (expressed in pence)
[number]  = nn.nn (with or without the decimal part)


Processing requirements
- All sales must be recorded
- All messages must be processed
- After every 10th message received your application should log a report detailing the number
of sales of each product and their total value.
- After 50 messages your application should log that it is pausing, stop accepting new
messages and log a report of the adjustments that have been made to each sale type while
the application was running.

Sales and Messages
- A sale has a product type field and a value – you should choose sensible types for these.
- Any number of different product types can be expected. There is no fixed set.
- A message notifying you of a sale could be one of the following types
    * Message Type 1 – contains the details of 1 sale E.g apple at 10p
    * Message Type 2 – contains the details of a sale and the number of occurrences of
        that sale. E.g 20 sales of apples at 10p each.
    * Message Type 3 – contains the details of a sale and an adjustment operation to be
        applied to all stored sales of this product type. Operations can be add, subtract, or
        multiply e.g Add 20p apples would instruct your application to add 20p to each sale
        of apples you have recorded.

