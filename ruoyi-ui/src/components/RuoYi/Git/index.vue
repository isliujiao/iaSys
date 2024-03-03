<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'IAGit',
  data() {
    return {
      url: 'https://gitee.com/isliujiao/ruoyi-project'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
