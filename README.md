# Project 4 - *InstagramClone*

**InstagramClone** is a photo sharing app using Parse as its backend.

Time spent: **20** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [X] User can sign up to create a new account using Parse authentication
- [X] User can log in to his or her account
- [X] The current signed in user is persisted across app restarts
- [x] User can log out of his or her account
- [x] User can take a photo, add a caption, and post it to "Instagram"
- [x] User can view the last 20 posts submitted to "Instagram"
- [x] User can pull to refresh the last 20 posts submitted to "Instagram"
- [x] User can tap a post to go to a Post Details activity, which includes timestamp and caption.
- [x] User sees app icon in home screen

The following **stretch** features are implemented:

- [ ] Style the login page to look like the real Instagram login page.
- [x] Style the feed to look like the real Instagram feed.
- [x] User can load more posts once he or she reaches the bottom of the feed using endless scrolling.
- [x] User should switch between different tabs using fragments and a Bottom Navigation View.
  - [x] Feed Tab (to view all posts from all users)
  - [x] Capture Tab (to make a new post using the Camera and Photo Gallery)
  - [ ] Profile Tab (to view only the current user's posts, in a grid)
- [x] Show the username and creation time for each post
- User Profiles:
  - [ ] Allow the logged in user to add a profile photo
  - [ ] Display the profile photo with each post
  - [ ] Tapping on a post's username or profile photo goes to that user's profile page
  - [ ] User Profile shows posts in a grid
- [ ] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- [ ] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1.Parse cloud functions
2.Efficiency of Glide caching with Parse's getURL() result

## Video Walkthrough

Here's a walkthrough of implemented user stories:
> Account creation and login
![User can login and create a new account](https://i.imgur.com/LqVZTpm.gif)

> Posting a pic and checking details + like
![Photo posting](https://i.imgur.com/9A16hTB.gif)

![](https://i.imgur.com/kajigni.gif)

GIF created with [Licecap](https://www.cockos.com/licecap/).

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library
- [Parse](https://parseplatform.org/)
- [Glide](https://bumptech.github.io/glide/)


## Notes

Not knowing the specifics of how Parse handles the database request kind of caused me to realize deleting a user but not their posts causes the app to crash.

Finding the camera implementation was rewarding but a bit hairy at first since there's a LOT of outdated guides. This only applies if you did not use the SDKs explained in the codepath guide, which is a nice guide. This project followed the official android guide to using CameraX.

## License

    Copyright 2021 Francisco Zamora Treviño

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
