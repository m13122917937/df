import Vue from 'vue'
import Cookies from 'js-cookie'

// 按需引入 Element UI 组件（babel-plugin-component 会在 production 模式下自动转换）
import 'element-ui/packages/theme-chalk/src/base.scss'

// 设置 Element UI 全局默认大小
Vue.prototype.$ELEMENT = { size: Cookies.get('size') || 'medium' }

// Layout
import { Row, Col } from 'element-ui'

// Basic
import {
  Button,
  Radio,
  RadioGroup,
  Checkbox,
  CheckboxGroup,
  Input,
  InputNumber,
  Select,
  Option,
  OptionGroup,
  Switch,
  Cascader,
  ColorPicker,
  DatePicker,
} from 'element-ui'

// Form
import { Form, FormItem } from 'element-ui'

// Data
import {
  Table,
  TableColumn,
  Tag,
  Progress,
  Tree,
  Pagination,
  Transfer,
} from 'element-ui'

// Notice
import { Alert } from 'element-ui'

// Navigation
import {
  Menu,
  MenuItem,
  MenuItemGroup,
  Submenu,
  Breadcrumb,
  BreadcrumbItem,
  Tabs,
  TabPane,
  Dropdown,
  DropdownMenu,
  DropdownItem,
  Steps,
  Step,
} from 'element-ui'

// Others
import {
  Dialog,
  Drawer,
  Popover,
  Tooltip,
  Popconfirm,
  Card,
  Collapse,
  CollapseItem,
  Descriptions,
  DescriptionsItem,
  Empty,
  Image,
  Link,
  Divider,
  Scrollbar,
  Skeleton,
  Backtop,
  Result,
  Timeline,
  TimelineItem,
  Upload,
  Autocomplete,
} from 'element-ui'

// 注册组件
const components = [
  Row, Col,
  Button, Radio, RadioGroup, Checkbox, CheckboxGroup, Input, InputNumber, Select, Option, OptionGroup, Switch, Cascader,
  ColorPicker, DatePicker,
  Form, FormItem,
  Table, TableColumn, Tag, Progress, Tree, Pagination, Transfer,
  Alert,
  Menu, MenuItem, MenuItemGroup, Submenu, Breadcrumb, BreadcrumbItem,
  Tabs, TabPane, Dropdown, DropdownMenu, DropdownItem,
  Steps, Step,
  Dialog, Drawer, Popover, Tooltip, Popconfirm, Card,
  Collapse, CollapseItem, Descriptions, DescriptionsItem,
  Empty, Image, Link, Divider, Scrollbar, Skeleton, Backtop, Result,
  Timeline, TimelineItem, Upload, Autocomplete,
]

components.forEach(component => {
  Vue.component(component.name, component)
})

// 全局指令 (Loading)
import Loading from 'element-ui/packages/loading'
Vue.use(Loading.directive)
Vue.prototype.$loading = Loading.service

// 全局方法 (Message, MessageBox, Notification)
import MessageBox from 'element-ui/packages/message-box'
Vue.prototype.$msgbox = MessageBox
Vue.prototype.$alert = MessageBox.alert
Vue.prototype.$confirm = MessageBox.confirm
Vue.prototype.$prompt = MessageBox.prompt

import Notification from 'element-ui/packages/notification'
Vue.prototype.$notify = Notification

import Message from 'element-ui/packages/message'
Vue.prototype.$message = Message
