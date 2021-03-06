# Inventory Application
Project 10 of 10 in the Udacity Android Basics Nanodegree

This was the final project of the Udacity course and so tried to encompass many of the features learned up to this point. The object of this project was create an inventory application that a store or business owner would use to keep track of bought and sold inventory. The backend of this application relies on  SQLite database to store the information about the items being tracked. For each item, the DB stored the name, selling price, current quantity, a description, and the location of a saved image file on the local device where a picture was located. 

The main screen of the app is where the items are all displayed in a scrolling list, and where each individual item has a "Sale" button that decreases the current quantity of that product by 1. If there are no items currently in the list, a FAB button is down in the bottom right corner to add a new item. Pressing that will take you a New Item screen where you can enter in the attributes required and save the item. 

If you were to click on an existing item in the main screen, a difference variation of the new item screen would open, where you could edit existing quantities and save them. The overflow menu in the top right will also give you the option to place an order for this product to your supplier. Pressing that will open up a pre-written email specifying what the product was and would only need the recipients email address and desired quantity to be filled in. 

Screenshots: 

![screenshot_1520820003](https://user-images.githubusercontent.com/14775517/37262461-990f13ac-2579-11e8-9805-cf23bfe3270b.png)

![screenshot_1520820389](https://user-images.githubusercontent.com/14775517/37262466-9dccb034-2579-11e8-9c3b-6a08f00b151f.png)

![screenshot_1520820366](https://user-images.githubusercontent.com/14775517/37262467-9fbe4e3e-2579-11e8-8f21-4753b444c28e.png)

![screenshot_1520820380](https://user-images.githubusercontent.com/14775517/37262468-a155f3f0-2579-11e8-8888-ab321a06cdc4.png)
