# 代码版本
https://github.com/android/platform_frameworks_support
master 2017-05-29
SHA: efdf532476a3639993c329de0a0d1f8c4fa53343

API 25

# 名词
* scroll方向
* 交叉方向
# 需要的功能
* 边缘弹动
* 横向 纵向
* overscroll 时 用手指滑动阻力 速度差
* 可配置最大overscroll距离
* 考虑SwipeRefreshLayout 下拉时的插值方式
* API 26 NestedScroll
* 设置overscroll时暴露区域的颜色 (设置overscroll区域的View?)
* 支持多children (在scroll方向需要有确定的宽度(支持WRAP_CONTENT MATCH_PARENT 固定大小)) scroll方向布局方式与LinearLayout相同
* 支持设置fling 阻力
* 支持设置OverScroll的一些参数
* 手指滑动与fling的overscroll具有不同的最大值?
* overscroll不同方向可配置不同的最大值?
## 下拉刷新Header
* 参考SwipeRefreshLayout的交互
* Header布局在头部或左部
## pagingEnabled
* 根据当前scroll方向 使用高度或宽度作为一页大小 可设置宽度或高度的百分比?
* 支持设置初始page
* 支持切换page 自动scroll到目标位置
* 支持关闭动画
* 支持设置一页的宽度
* 支持设置初始页的偏移
* 支持设置fling 之后停止的方式 只允许滑动一页 或者自然滑动直到停止然后对齐到某一页

# 滑动边界条件
```
maxOver: 最大overscroll距离
  |   A    |
  |--------| <- y = - maxOver
  |   B    |
  |--------| <- y = 0
  |        |
  |   C    |
  |        |
  |--------| <- y = scrollRange
  |   D    |
  |--------| <- y = scrollRange + maxOver
  |   E    |
```
##