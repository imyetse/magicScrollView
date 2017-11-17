# magicScrollView
## Abstract 摘要

粘性下拉显示header，上拉显示内容，类似魅族便签下拉显示标题的效果，合适需要在scrollview添加header的场景使用

## 效果预览

![Image text](https://github.com/imyetse/magicScrollView/blob/master/gif/demo.gif)

## Usage使用方法
```
<tse.ye.demo.lib.StickyScrollView
        android:id="@+id/scrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:showHeader="false">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <tse.ye.demo.demos.GGBanner
                android:id="@+id/banner"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginLeft="30dp"
                android:layout_marginRight="30dp"
                android:clipChildren="false" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_margin="@dimen/text_margin"
                android:text="@string/large_text" />
        </LinearLayout>

    </tse.ye.demo.lib.StickyScrollView>
```
当做普通的ScrollView使用，要保证<LinearLayout>下面至少一个子View
  第一个子View会默认为Header
  
 ## 属性方法
app:showHeader="false" //是否默认显示header view

app:animateEnable="false" //是否使用header view显示隐藏时的动画

app:animateDuration="250" //HeaderView显示隐藏时的动画时长

app:scrollFraction="0.4" //我称之为粘性滑动的比值 比如手指滑动10个像素，实际只是滑动4个像素


欢迎fork改进

 ## LICENSE 开源协议

Apache License Version 2.0 
