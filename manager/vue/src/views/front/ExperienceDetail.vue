<template>
  <div class="main-content">
    <div style="width: 80%; margin: 30px auto">
      <div class="back-btn" @click="goBack">
        <el-button type="primary" plain>
          <i class="el-icon-back"></i> 返回列表
        </el-button>
      </div>
      
      <div class="experience-detail">
        <h1 class="experience-title">{{ experience.name }}</h1>
        
        <div class="experience-meta">
          <span class="meta-item">
            <el-tag :type="experience.role === 'COACH' ? 'primary' : 'info'">
              {{ experience.role === 'COACH' ? '教练' : '用户' }}
            </el-tag>
          </span>
          <span class="meta-item">发布人：{{ experience.userName }}</span>
          <span class="meta-item">发布时间：{{ experience.time }}</span>
        </div>
        
        <div class="experience-content" v-html="experience.content"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      experience: {
        name: '',
        userName: '',
        role: '',
        time: '',
        content: ''
      }
    }
  },
  created() {
    this.loadExperience()
  },
  methods: {
    loadExperience() {
      const id = this.$route.query.id
      if (id) {
        this.$request.get('/experience/selectById/' + id).then(res => {
          if (res.code === '200') {
            this.experience = res.data
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    },
    goBack() {
      this.$router.push('/front/Experience')
    }
  }
}
</script>

<style scoped>
.main-content {
  min-height: 700px;
}

.back-btn {
  margin-bottom: 30px;
}

.experience-detail {
  background-color: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.experience-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.experience-meta {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eaeaea;
  font-size: 14px;
  color: #666;
}

.meta-item {
  margin-right: 20px;
}

.experience-content {
  line-height: 1.8;
  color: #333;
  font-size: 16px;
}

.experience-content img {
  max-width: 100%;
  height: auto;
  margin: 10px 0;
}

.experience-content p {
  margin-bottom: 15px;
}

.experience-content h1,
.experience-content h2,
.experience-content h3 {
  margin: 20px 0 10px 0;
  color: #333;
}

.experience-content ul,
.experience-content ol {
  margin: 10px 0;
  padding-left: 20px;
}

.experience-content li {
  margin-bottom: 5px;
}
</style>