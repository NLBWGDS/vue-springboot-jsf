<template>
    <div class="main-content">
        <div style="width: 80%; margin: 20px auto">
            <div style="font-size: 17px; color: #666666">课程预约记录（{{ total }}）</div>
            <div style="margin-top: 20px">
                <el-input placeholder="请输入课程名称查询" style="width: 300px" v-model="courseName"></el-input>
                <el-button type="primary" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
                <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
            </div>
            <div style="margin-top: 20px">
                <el-table :data="tableData" stripe>
                    <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
                    <el-table-column prop="courseName" label="课程名称" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="userName" label="用户姓名" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="time" label="下单时间"></el-table-column>
                    <el-table-column prop="price" label="课程价格" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="status" label="订单状态">
                        <template v-slot="scope">
                            <el-tag v-if="scope.row.status === 'UNCOMPLETED'" type="warning">未完成</el-tag>
                            <el-tag v-else-if="scope.row.status === 'COMPLETED'" type="success">已完成</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="refundStatus" label="退款状态">
                        <template v-slot="scope">
                            <el-tag v-if="scope.row.refundStatus === 'NONE'" type="info">无退款</el-tag>
                            <el-tag v-else-if="scope.row.refundStatus === 'APPLYING'" type="warning">申请退款</el-tag>
                            <el-tag v-else-if="scope.row.refundStatus === 'APPROVED'" type="success">退款通过</el-tag>
                            <el-tag v-else-if="scope.row.refundStatus === 'REJECTED'" type="danger">退款拒绝</el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" width="200" align="center">
                        <template v-slot="scope">
                            <el-button v-if="scope.row.status === 'UNCOMPLETED' && scope.row.refundStatus === 'NONE'" plain type="success" size="mini" @click="completeOrder(scope.row.id)">完成课程</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="pagination" style="margin-top: 20px">
                    <el-pagination
                        background
                        @current-change="handleCurrentChange"
                        :current-page="pageNum"
                        :page-sizes="[5, 10, 20]"
                        :page-size="pageSize"
                        layout="total, prev, pager, next"
                        :total="total">
                    </el-pagination>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

    export default {

    data() {
    return {
    tableData: [],  // 所有的数据
    pageNum: 1,   // 当前的页码
    pageSize: 10,  // 每页显示的个数
    total: 0,
    courseName: null,
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
}
},
    mounted() {
    this.load(1)
},
    // methods：本页面所有的点击事件或者其他函数定义区
    methods: {
    load(pageNum) {
    if (pageNum) this.pageNum = pageNum
    const params = {
      pageNum: this.pageNum,
      pageSize: this.pageSize,
      courseName: this.courseName
    }
    if (this.user && this.user.id) {
      params.coachId = this.user.id
    }
    console.log('请求参数:', params)
    this.$request.get('/orders/selectPage', {
      params: params
    }).then(res => {
      console.log('响应数据:', res)
      this.tableData = res.data?.list || []
      this.total = res.data?.total || 0
    })
}, 
    reset() {
    this.courseName = null
    this.load(1)
},
    handleCurrentChange(pageNum) {
    this.pageNum = pageNum
    this.load(this.pageNum)
},
    completeOrder(id) {
    this.$confirm('您确定完成该课程吗？', '确认完成', {type: "success"}).then(response => {
    this.$request.put('/orders/complete/' + id).then(res => {
    if (res.code === '200') {
    this.$message.success('操作成功')
    this.load(1)
} else {
    this.$message.error(res.msg)
}
})
}).catch(() => {
})
}
}
}
</script>

<style scoped>

</style>