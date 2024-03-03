<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'IADoc',
  data() {
    return {
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
