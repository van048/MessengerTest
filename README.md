# 场景：如果一个应用要从网络上下载MP3文件，并在Activity上展示进度条，这个Activity要求是可以转屏的。那么在转屏时Actvitiy会重启，如何保证下载的进度条能正确展示进度呢？

## Client
Call `unBind` when download completes.
## Server
Make sure the working thread not affected by the client's `Activity`.
