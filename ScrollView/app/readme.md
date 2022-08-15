# 代码版本
https://github.com/android/platform_frameworks_support
NestedScrollView 与support 27.0.2 代码相同

API 27

# 名词
* scroll方向
* 交叉方向

# 需要的功能
* 边缘弹动
* 横向 纵向
* overscroll 时 用手指滑动阻力 速度差
* 可配置最大overscroll距离
* overscroll 时touch 滑动与实际滑动有比例 放手时速度也要考虑比例 参考SwipeRefreshLayout 下拉时的插值方式 比例分段使用不同的值
* API 26 NestedScroll
* 设置overscroll时暴露区域的颜色 两端可分别设置 (设置overscroll区域的View?)
* 支持多children (在scroll方向需要有确定的宽度(支持WRAP_CONTENT MATCH_PARENT 固定大小)) scroll方向布局方式与LinearLayout相同
* 支持设置fling 阻力
* 支持设置OverScroll的一些参数
* 手指滑动与fling的overscroll具有不同的最大值?
* overscroll不同方向可配置不同的最大值?
* 可以设置overscroll模式 EdgeEffect overscroll 或没有
* 可设置手势捕获的灵敏度 参考ViewPager

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

## 使用RecyclerView实现
* 增加LayoutManager touch事件相关的钩子 (如判断touch是否需要开始)
* 去除RecyclerView对api 14以下的兼容代码
* 实现ScrollViewLayoutManager 用scroll实现滚动

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
## touch 允许OverScroll
### 在C与B交界及以上 向下滑动 如果NestedScrollingParent 消耗之后还有剩余 则进入B或继续向下在OverScroll区域滑动
### 在B及以上 向上滑动 在dispatchNestedPreScroll之前 优先滑动知道B与C交界 将剩余的传给dispatchNestedPreScroll
### B与A交界及以上 向下滑动 无动作

## fling 允许OverScroll api 26 NestedScroll2
### 在OverScroll区域内的fling 不调用 dispatchNestedPreScroll dispatchNestedScroll TYPE_NON_TOUCH
### C 速度向下 scroll 减小
* 在C与B交界时如果hasNestedScrollingParent 且本次fling过程中NestedScrollingParent 消耗过则不进行OverScroll?
* 否则进行OverScroll
### B及以上 速度向下 scroll 减小
#### dispatchNestedPreFling 返回false
* 进行OverScroll区域的fling
#### dispatchNestedPreFling 返回true
* dispatchNestedPreFling 进行springback
### B与A交界及以上 速度向下 scroll 减小  无动作
### B及以上 速度向上 scroll 增大
* 进行OverScroll区域的fling

### 在OverScroll区域的fling 不调用dispatchNestedPreFling 在fling进入C时才调用

## springback
* springback 过程中 不调用 dispatchNestedPreScroll dispatchNestedScroll TYPE_NON_TOUCH

## dx dy 问题 以y方向为例
touch 向下 y增大
scroll 向下 scrollY 减小
dispatchNestedScroll dy 采用 scroll 相同的方式 向下 为负