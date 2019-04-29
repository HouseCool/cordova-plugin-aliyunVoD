# aliyun-VOD 
> This plugin is Ali cloud video on-demand file upload function.

## How to Install

Cordova:
```bash
cordova plugin add https://github.com/HouseCool/cordova.HouseCoolPlugins.photoviewer.git
```

### Android
> Out of the box

#### Show an image

```
PhotoViewer.show('http://my_site.com/my_image.jpg', 'Optional Title');
```

Optionally you can pass third parameter option as object.

Options:
* share: Image download function.

##### Usage

```
var options = {
    share: true, // default is false
};

PhotoViewer.show('http://my_site.com/my_image.jpg', 'Optional Title', options);
```
### API-Android

URL:
* title: Picture captions [mandatory]
* description : Picture description [optional]
* url: Picture address 

```
function Transfer(){
    var url = [
        {"title":"图像1","description"："图片描述","url":"https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1982513715,1507401127&fm=26&gp=0.jpg"},
    ]
    var options = {
        share: false, // default is false
    };
    PhotoViewer.showMultiple(url,3,options);
}
```


