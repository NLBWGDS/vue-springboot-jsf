<template>
    <div>
        <div class="search">
            <el-input placeholder="请输入订单编号" style="width: 200px" v-model="orderNo"></el-input>
            <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
        <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
</div>

<div class="operation">
    <el-button type="danger" plain @click="delBatch">批量删除</el-button>
</div>

<div class="table">
    <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" align="center"></el-table-column>
    <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
    <el-table-column prop="courseName" label="课程名称" show-overflow-tooltip></el-table-column>
    <el-table-column prop="coachName" label="教练姓名" show-overflow-tooltip></el-table-column>
    <el-table-column prop="userName" label="下单用户" show-overflow-tooltip></el-table-column>
    <el-table-column prop="orderNo" label="订单编号" show-overflow-tooltip></el-table-column>
    <el-table-column prop="price" label="课程价格" show-overflow-tooltip></el-table-column>
    <el-table-column prop="time" label="下单时间"></el-table-column>
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
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
            <el-button v-if="scope.row.refundStatus === 'APPLYING'" plain type="success" size="mini" @click=approveRefund(scope.row.id)>通过</el-button>
            <el-button v-if="scope.row.refundStatus === 'APPLYING'" plain type="danger" size="mini" @click=rejectRefund(scope.row.id)>拒绝</el-button>
    </template>
</el-table-column>
</el-table>

<div class="pagination">
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
</template>

<script>
    export default {
    name: "Orders",
    data() {
    return {
    tableData: [],  // 所有的数据
    pageNum: 1,   // 当前的页码
    pageSize: 10,  // 每页显示的个数
    total: 0,
    orderNo: null,
    fromVisible: false,
    form: {},
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    rules: {},
    ids: []
}
},
    created() {
    this.load(1)
},
    methods: {
    del(id) {   // 单个删除
    this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
    this.$request.delete('/orders/delete/' + id).then(res => {
    if (res.code === '200') {   // 表示操作成功
    this.$message.success('操作成功')
    this.load(1)
} else {
    this.$message.error(res.msg)  // 弹出错误的信息
}
})
}).catch(() => {
})
},
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
    this.ids = rows.map(v => v.id)   //  [1,2]
},
    delBatch() {   // 批量删除
    if (!this.ids.length) {
    this.$message.warning('请选择数据')
    return
}
    this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
    this.$request.delete('/orders/delete/batch', {data: this.ids}).then(res => {
    if (res.code === '200') {   // 表示操作成功
    this.$message.success('操作成功')
    this.load(1)
} else {
    this.$message.error(res.msg)  // 弹出错误的信息
}
})
}).catch(() => {
})
},
    load(pageNum) {  // 分页查询
    if (pageNum) this.pageNum = pageNum
    this.$request.get('/orders/selectPage', {
    params: {
    pageNum: this.pageNum,
    pageSize: this.pageSize,
    orderNo: this.orderNo,
}
}).then(res => {
    this.tableData = res.data?.list
    this.total = res.data?.total
})
},
    reset() {
    this.orderNo = null
    this.load(1)
},
    handleCurrentChange(pageNum) {
    this.load(pageNum)
},
    approveRefund(id) {
        this.$confirm('您确定通过该退款申请吗？', '确认通过', {type: "success"}).then(response => {
            this.$request.put('/orders/auditRefund', {id: id, status: 'APPROVED'}).then(res => {
                if (res.code === '200') {
                    this.$message.success('操作成功')
                    this.load(1)
                } else {
                    this.$message.error(res.msg)
                }
            })
        }).catch(() => {
        })
    },
    rejectRefund(id) {
        this.$confirm('您确定拒绝该退款申请吗？', '确认拒绝', {type: "warning"}).then(response => {
            this.$request.put('/orders/auditRefund', {id: id, status: 'REJECTED'}).then(res => {
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