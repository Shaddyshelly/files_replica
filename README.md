# Files_google_replica

In this mobile application I have tried to make replica of Files application by GOOGLE.
In this app user can see all photos, videos, audios, documents, downloads and also internal storage folders and files

<img src="https://github.com/amritagg/Files_google_replica/blob/master/screenshots/HOME.jpg" height=1000 />

## FUNCTIONALITIES
As soon as user opens the application first it asks for permissions to view files in the memory. (This is only one time task, if user provide access to this then the next time he/she opens the app user don't have to again give permission since it's already given)

Home screen is divided into 2 sections.

First one is <b>CATEGORIES</b>
Second is <b>INTERNAL STORAGE</b>

### CATEGORIES

This section consists of <b>Download, Images, Videos, Audios, Documents</b>
As the user select anyone of these user can see all the files of that category.

For everyone of these user have access to show in <b>Grid-View</b> and <b>List-View</b> and you can also sort the files in the way you want to see. Like according to size, date of creation or alphabetically

<img src="https://github.com/amritagg/Files_google_replica/blob/master/screenshots/SORT.jpg" height=1000 />

In the files are being shown in List-View user can use additional functionality by clicking on menu option at the end of each file, from where they can share file, open it with other application, see file information or can even delete file.

<img src="https://github.com/amritagg/Files_google_replica/blob/master/screenshots/LIST.jpg" height=1000 />

Now you can also play show file individually like in case of images you can see individual image on clicking onto it or like in case of audio or video you can play them and also in case of PDF files you can see PDf file using in-built PDF viewer of the app.

<img src="https://github.com/amritagg/Files_google_replica/blob/master/screenshots/IMAGE.jpg" height=1000 />

User can also get the functionality of share, delete and information while on that file page.

On clicking Info button Information of that file is shown

<img src="https://github.com/amritagg/Files_google_replica/blob/master/screenshots/INFO.jpg" height=1000 />

### INTERNAL STORAGE

In this section user can see all the folders and files from home directory of the device in List-View.

<img src="https://github.com/amritagg/Files_google_replica/blob/master/screenshots/INTERNAL.jpg" height=1000 />

User can change if he/she wants to see private files/folders or not by using menu option
On clicking on folder user can see all the folders and files in that folder on which they clicked.

If it contains files like image, video, audio, pdf then can also view it like before.


## WHAT DID I USED

This application consists of different libraries and techniques.
For the user permissions files permission can be given simply by alert dialog in case of Android user 10 but in case of Android user 11 or higher MANAGE_INTERNAL_STORAGE permission needs to be accepted for which <b>ActivityResultLauncher&lt;Intent&gt;</b> is used which asks for manage files permission in settings.

In Categories section files are being loaded using <b>MEDIASTORE</b> API of Android which works for Android version 10 and higher that's why this Application works only in case of Android 10 and higher.

For loading files in background <b>LoaderManger</b> are used so that the screen of the user didn't get stuck when he openup a category. Also a circular progress bar is shown till all the files are loaded and then all the files are shown on the activity using <b>RecyclerView</b> for which <b>Adapters</b> are used so that only the files which are visible to user are loaded only. Otherwise it will effect the user experience.

For Loading Images <b>Glide</b> library is used which loads the image by taking the context, file_path/uri and imageview and in the ImageAcitivity all the images are shown using <b>ViewPager2</b> which gives the functionality to slide the current image to see next/previous image.

For playing audio and video files <b>ExoPlayer</b> is used because this is like an abstract layer above <b>MediaPlayer</b> which result in better user experience.

Thumbnails of images and videos and loaded using Glide but thumbnails of Audio files are loaded manually using LoaderManager background task. After loading images they all are saved using <b>LRU Cache</b> memory which result in better user experience.

For showing sort options <b>BottomSheetDialog</b> is used which scrolls up a sheet from bottom of the activty which contains the available options.

For showing PDF files <b>ParcelFileDescriptor</b> and <b>PdfRenderer</b> are used which loads all the pages of the PDF in form of images and then all those images are saved in LRU cache.

All the folder and files of the home directory are show using home directory as <b>/storage/emulated/0</b> and using listFiles function which gives the File array of the childer files/folders.

Using all these Methods, this app is build successfully

<H2>THANK YOU!!</H2>
