Questions to answer:

1. Does the project appear to meet the technical requirements? Write up one sentence on your findings and give a score 0-3.
  stew: 2. looks like you have everything you need except for a background thread for database interactions or api calls might help.

Is your peer making API calls, using SDK's/third-party libraries?
  Stew: Yes, the app's using the retrofit library and multiple apis

Is your peer making use of Services? If so, are they offloading long tasks to a separate thread, i.e. AsyncTask, Runnable, IntentService, etc.
  Stew: the app makes API calls with the help of separate service classes for Rotten Tomatoes and Four Square. The app makes use of a firebase db, so there may be some opportunities for running interacting w the db on a separate thread if you notice its slowing the app down.

Is your peer making use of Fragments? If so, are they passing data from Fragment to Activity via interfaces? If not, why did absense of Fragments make sense?
  Stew: I don't see any use of Fragments but it may or may not make sense for your app. There may be an opportunity to make use of fragments for displaying the various restaurants, bars, etc if it makes sense for the user flow.
  
Is your peer making use of RecyclerView? If so, does it appear to be working correctly ( implementation and otherwise )?
  Stew: Yes
Is your peer making use of some sort of persistent storage, i.e. Firebase or SQLite? If so, why do you think Firebase/SQLite was chosen? Could they have used one or the other instead and why?
  Stew: yes, nice use of firebase. definitely makes sense for your idea since multiple people will be interacting with eachother on the app so they'll both probably be writing to the same db which is easier to do with firebase

2. Does the project appear to be creative, innovative, and different from any competition? Write up one sentence on your findings and give a score 0-3.
  stew: 3. yes, really cool idea that builds upon the popularity of tinder.

Is your peer making use of proper UX patterns we learned in class? If not, what are they doing that is unconvetional or that might confuse a user ( you )?
 stew: love the animation on the main screen, i've never used tinder, but seems like its the same pattern in your app. very cool and clever. i also like that you have the redundant red x and green check just to make sure its obvious to the user what they should do, it gives them a few options. The drawer is useful too. having a location switch is nice.

Is your peer making anything cool or awesome that you would like to note or applaud them on?
  stew: the swipe left, swipe right is awesome.
3. Does the project appear to follow correct coding styles and best practices? Write up one sentence on your findings and give a score 0-3.
  stew:  3. your code is really clean, and easy to read. 

Are you able to reasonably follow the code without having anyone answer your questions?
  stew: yes
  
Are you able to make sense of what the code is doing or is trying to do?
  stew: yes
  
4. Find two pieces of code of any size: one that is readable and easy to follow and one that is difficult to follow and understand.

Readable code:

/**
     * Switches the associated booleans with the switch when the switch is toggled
     * @param s
     */
    private void checkVenueSwitches(final Switch s) {
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggled = s.isChecked();
                switch (s.getId()) {
                    case R.id.food_search_switch:
                        if (toggled) {
                            isFoodQueryToggle = true;
                        } else {
                            isFoodQueryToggle = false;
                        }
                        break;
                    case R.id.drink_search_switch:
                        if (toggled) {
                            isDrinkQueryToggle = true;
                        } else {
                            isDrinkQueryToggle = false;
                        }
                        break;
                    case R.id.activities_search_switch:
                        if (toggled) {
                            isLocationQueryToggle = true;
                        } else {
                            isLocationQueryToggle = false;
                        }
                        break;
                    case R.id.events_search_switch:
                        if (toggled) {
                            isEventsQueryToggle = true;
                        } else {
                            isEventsQueryToggle = false;
                        }
                        break;
                    default:
                }
            }
        });
    }
  Stew: the code above is very easy to follow even though it's on the long side. In this case, the length of the method isn't a negative because there are multiple cases to account for. The comment before the method sets things up nicely, making it very easy to understand what each line of code in the subsequent method is actually doing.
  
Difficult to read code:

I tried finding ugly code, and couldn't find any, it's all commented well and methods are names approproiately. nice job.

What makes the readable code readable? Be as detailed as you can in your answer, it can be challenging to explain why something is easy to undertand
What makes the difficult code harder to follow? Be as detailed as you can in your answer.
5. High level project overview: Take a look at as many individual files as you have time for

Does this class make sense?
Does the structure of the class make sense?
Is it clear what this class is supposed to do?
Status API Training Shop Blog About
© 2016 GitHub, Inc. Terms Privacy Security Contact Help
